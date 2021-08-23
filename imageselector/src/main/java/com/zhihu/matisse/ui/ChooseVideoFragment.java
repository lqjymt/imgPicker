/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.model.AlbumVideoCollection;
import com.zhihu.matisse.internal.model.SelectedItemCollection;
import com.zhihu.matisse.internal.model.SelectedVideoItemCollection;
import com.zhihu.matisse.internal.ui.AlbumPreviewActivity;
import com.zhihu.matisse.internal.ui.BasePreviewActivity;
import com.zhihu.matisse.internal.ui.MediaSelectionFragment;
import com.zhihu.matisse.internal.ui.VideoSelectionFragment;
import com.zhihu.matisse.internal.ui.adapter.AlbumImageMediaAdapter;

/**
 * 图片和视频在同一个fragment展示
 */
public class ChooseVideoFragment extends Fragment implements
    MediaSelectionFragment.SelectionProvider,
    AlbumImageMediaAdapter.CheckStateListener, AlbumImageMediaAdapter.OnMediaClickListener,
    AlbumVideoCollection.AlbumCallbacks {

    private static final int REQUEST_CODE_PREVIEW = 23;
    private final AlbumVideoCollection mAlbumCollection = new AlbumVideoCollection();
    private SelectedItemCollection mSelectedCollection;
    
     public static ChooseVideoFragment newInstance() {
         return new ChooseVideoFragment();
       }

    protected AppCompatActivity mContext;
    @Override public void onAttach(@NonNull  Context context) {
        mContext = (AppCompatActivity) context;
        super.onAttach(context);
    }


    private View mRootView = null ;
    private View mContainer;
    private View mEmptyView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if (mRootView == null) {
            mRootView = inflater.inflate(this.getLayoutId(), container, false);

        } else {
            //  二次加载删除上一个子view
            ViewGroup viewGroup = (ViewGroup) mRootView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mRootView);
            }
        }

        initView(mRootView);
        return mRootView;
    }

    private void initView(View mRootView) {
        mContainer = mRootView.findViewById(R.id.video_container);
        mEmptyView = mRootView.findViewById(R.id.empty_view);
        mSelectedCollection = new SelectedItemCollection(mContext);
        mSelectedCollection.onCreate(null);

        mAlbumCollection.onCreate(mContext, this);
        mAlbumCollection.loadAlbums();
    }

    private int getLayoutId() {
        return R.layout.fragment_choose_video;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
        mAlbumCollection.onSaveInstanceState(outState);
    }
    public SelectedVideoItemCollection getSelectData(){
        if (fragment != null) {
            return fragment.getData();
        }else{
            return  null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAlbumCollection.onDestroy();

    }
    private VideoSelectionFragment fragment;

    private void onAlbumSelected(Album album) {
        if (album.isAll() && album.isEmpty()) {
            mContainer.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mContainer.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);

             fragment = VideoSelectionFragment.newInstance(album);
           getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.video_container, fragment, VideoSelectionFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onUpdate() {
        // notify bottom toolbar that check state changed.

        //if (mSpec.onSelectedListener != null) {
        //    mSpec.onSelectedListener.onSelected(
        //            mSelectedCollection.asListOfUri(), mSelectedCollection.asListOfString());
        //}
    }

    @Override
    public void onMediaClick(Album album, Item item, int adapterPosition) {
        Intent intent = new Intent(mContext, AlbumPreviewActivity.class);
        intent.putExtra(AlbumPreviewActivity.EXTRA_ALBUM, album);
        intent.putExtra(AlbumPreviewActivity.EXTRA_ITEM, item);
        intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
        intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, false);
        startActivityForResult(intent, REQUEST_CODE_PREVIEW);
    }

    @Override
    public SelectedItemCollection provideSelectedItemCollection() {
        return mSelectedCollection;
    }

    @Override public void onAlbumLoad(final Cursor cursor) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                cursor.moveToPosition(mAlbumCollection.getCurrentSelection());
                Album album = Album.valueOf(cursor);
                onAlbumSelected(album);
            }
        });
    }

    @Override public void onAlbumReset() {

    }
}
