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

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.zhihu.matisse.R;
import com.zhihu.matisse.engine.MultiPagerAdapter;
import com.zhihu.matisse.event.OnSelectBothEvent;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.model.AlbumCollection;
import com.zhihu.matisse.internal.model.SelectedImageItemCollection;
import com.zhihu.matisse.internal.model.SelectedVideoItemCollection;
import com.zhihu.matisse.internal.ui.PhotoSelectionFragment;
import com.zhihu.matisse.internal.ui.VideoSelectionFragment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 图片和视频在同一个页面展示
 */
public class MatisseBothActivity extends AppCompatActivity implements View.OnClickListener
    , VideoSelectionFragment.SelectionVideoProvider, PhotoSelectionFragment.SelectionProvider{

  public static final String EXTRA_RESULT_SELECTION = "extra_result_selection";
  public static final String EXTRA_RESULT_SELECTION_PATH = "extra_result_selection_path";
  public static final String EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable";
  private final AlbumCollection mAlbumCollection = new AlbumCollection();
  private SelectedImageItemCollection mSelectedCollection = new SelectedImageItemCollection(this);
  private SelectedVideoItemCollection mSelectedVideoCollection = new SelectedVideoItemCollection(this);
  private SelectionSpec mSpec;

  //private CommonTabLayout tabLayout;
  private ViewPager viewPager;
  private ChooseVideoFragment chooseVideoFragment;
  private ChooseImageFragment chooseImageFragment;
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    mSpec = SelectionSpec.getInstance();
    setTheme(mSpec.themeId);
    super.onCreate(savedInstanceState);
    if (!mSpec.hasInited) {
      setResult(RESULT_CANCELED);
      finish();
      return;
    }
    setContentView(R.layout.activity_matisse_both);

  findViewById(R.id.button_apply).setOnClickListener(this);
  findViewById(R.id.img_back).setOnClickListener(this);
    if (mSpec.needOrientationRestriction()) {
      setRequestedOrientation(mSpec.orientation);
    }
    mSelectedCollection.onCreate(null);
    mSelectedVideoCollection.onCreate(null);


    viewPager = findViewById(R.id.container);
    ArrayList<Fragment> mFragments = new ArrayList<>();
     chooseVideoFragment = ChooseVideoFragment.newInstance();
     //chooseImageFragment = ChooseImageFragment.newInstance();
    mFragments.add(chooseVideoFragment);
    //mFragments.add(chooseImageFragment);
    viewPager.setAdapter(new MultiPagerAdapter(getSupportFragmentManager(), mFragments));

    //tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
    //  @Override public void onTabSelect(int position) {
    //    viewPager.setCurrentItem(position);
    //  }
    //
    //  @Override public void onTabReselect(int position) {
    //
    //  }
    //});
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        //tabLayout.setCurrentTab(position);
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mSelectedCollection.onSaveInstanceState(outState);
    mSelectedVideoCollection.onSaveInstanceState(outState);
    mAlbumCollection.onSaveInstanceState(outState);
    outState.putBoolean("checkState", false);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mAlbumCollection.onDestroy();
    mSpec.onCheckedListener = null;
    mSpec.onSelectedListener = null;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    setResult(Activity.RESULT_CANCELED);
    super.onBackPressed();
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.button_apply) {
      boolean isVideo = true;
      if (isVideo) {
        ArrayList<String> selectedPaths = null;
        SelectedVideoItemCollection selectData = chooseVideoFragment.getSelectData();
        if (selectData != null) {
          selectedPaths = (ArrayList<String>)selectData .asListOfString();
        }
        long  duration = 0;
        if (selectedPaths != null && selectedPaths.size() > 0) {
          try {
            File file = new File(selectedPaths.get(0));
            MediaPlayer meidaPlayer = new MediaPlayer();
            meidaPlayer.setDataSource(file.getPath());
            meidaPlayer.prepare();
            duration = meidaPlayer.getDuration();
            if (duration <  1000) {
              return;
            }
            if (duration >  60*1000) {
              return;
            }
         } catch (IOException e) {
            e.printStackTrace();
          }
          OnSelectBothEvent onSelectBothEvent = new OnSelectBothEvent(selectedPaths, true);
          onSelectBothEvent.setDuration(duration);
        }
        finish();
      }else{
        ArrayList<String> selectedPaths = null;
        SelectedImageItemCollection selectData = chooseImageFragment.getSelectData();
        if (selectData != null) {
          selectedPaths = (ArrayList<String>) selectData.asListOfString();
        }
        if (selectedPaths != null && selectedPaths.size() > 0) {
        }
        finish();
      }
    }else if(v.getId() == R.id.img_back){
      finish();
    }
  }

  @Override public SelectedImageItemCollection provideSelectedItemCollection() {
    return mSelectedCollection;
  }

  @Override public SelectedVideoItemCollection provide() {
    return mSelectedVideoCollection;
  }
}
