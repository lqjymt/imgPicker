package com.zhihu.matisse.event;

import java.util.ArrayList;

/**
 * Copyright (C), 2015-2020, 华苗木云有限公司
 *
 * @author : lqj
 * Date : 2020/6/19 09:40
 */
public class OnSelectBothEvent {
  private ArrayList<String> selectedPaths;
  private boolean isVideo;
  private long duration;

  public OnSelectBothEvent(ArrayList<String> selectedPaths, boolean isVideo) {
    this.selectedPaths = selectedPaths;
    this.isVideo = isVideo;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public ArrayList<String> getSelectedPaths() {
    return selectedPaths;
  }

  public void setSelectedPaths(ArrayList<String> selectedPaths) {
    this.selectedPaths = selectedPaths;
  }

  public boolean isVideo() {
    return isVideo;
  }

  public void setVideo(boolean video) {
    isVideo = video;
  }
}
