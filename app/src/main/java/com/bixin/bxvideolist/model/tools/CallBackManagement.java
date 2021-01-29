package com.bixin.bxvideolist.model.tools;

import com.bixin.bxvideolist.model.listener.OnUpdateListener;

/**
 * @author Altair
 * @date :2020.04.01 上午 10:23
 * @description:
 */
public class CallBackManagement {
    private OnUpdateListener mOnUpdateListener;

    public static class SingleHolder {
        static CallBackManagement management = new CallBackManagement();
    }

    public static CallBackManagement getInstance() {
        return SingleHolder.management;
    }

    public void setOnUpdateListener(OnUpdateListener listener) {
        this.mOnUpdateListener = listener;
    }

    public void updatePicture(String path) {
        if (mOnUpdateListener != null) {
            mOnUpdateListener.updatePicture(path);
        }
    }

}
