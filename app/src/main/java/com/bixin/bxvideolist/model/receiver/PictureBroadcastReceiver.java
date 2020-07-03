package com.bixin.bxvideolist.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bixin.bxvideolist.view.activity.ShowPictureActivity;

import java.lang.ref.WeakReference;

public class PictureBroadcastReceiver extends BroadcastReceiver {
    private WeakReference<ShowPictureActivity> activityWeakReference;
    private ShowPictureActivity pictureActivity;

    public PictureBroadcastReceiver(ShowPictureActivity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
        pictureActivity = activityWeakReference.get();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("CLOSE_VIDEO_APP")) {
            pictureActivity.finish();
        }
    }
}
