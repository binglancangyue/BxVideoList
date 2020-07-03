package com.bixin.bxvideolist.view.activity;

import android.app.Application;

import com.bixin.bxvideolist.model.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Altair
 * @date :2019.11.22 下午 03:14
 * @description:
 */
public class MyApplication extends Application {
    private static MyApplication myApplication;
    private List<VideoBean> normalVideoList = new ArrayList<>();
    private List<VideoBean> lockVideoList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    public void setNormalVideoList(List<VideoBean> videoList) {
        this.normalVideoList = videoList;
    }

    public void setLockVideoList(List<VideoBean> videoList) {
        this.lockVideoList = videoList;
    }

    public List<VideoBean> getNormalVideoList() {
        return normalVideoList;
    }

    public List<VideoBean> getLockVideoList() {
        return lockVideoList;
    }

    public static MyApplication getInstance() {
        return myApplication;
    }
}
