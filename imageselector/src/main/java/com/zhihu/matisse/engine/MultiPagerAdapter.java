package com.zhihu.matisse.engine;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;

/**
 * Copyright (C), 2015-2020, 华苗木云有限公司
 * @author : lqj
 * Date : 2020/4/22 16:11
 */
public class MultiPagerAdapter extends FragmentPagerAdapter {
  private ArrayList<Fragment> mFragments ;
  public MultiPagerAdapter(@NonNull FragmentManager fm,ArrayList<Fragment> fragments) {
    super(fm);
    mFragments = fragments;
  }

  @Override
  public int getCount() {
    return mFragments.size();
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return "";
  }

  @NonNull @Override
  public Fragment getItem(int position) {
    return mFragments.get(position);
  }
}
