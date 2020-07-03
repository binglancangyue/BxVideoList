package com.bixin.bxvideolist.model.tools;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Altair
 * @date :2019.12.26 下午 02:16
 * @description:
 */
public class DismissControlTimer extends TimerTask {
    protected static Timer DISMISS_CONTROL_VIEW_TIMER;
    private DismissControlTimer mDismissControlViewTimerTask;
    private Handler mHandle;

    public void startDismissControlViewTimer(Handler handler) {
        this.mHandle = handler;
        cancelDismissControlViewTimer();
        DISMISS_CONTROL_VIEW_TIMER = new Timer();
        mDismissControlViewTimerTask = new DismissControlTimer();
        DISMISS_CONTROL_VIEW_TIMER.schedule(mDismissControlViewTimerTask, 2500);
    }

    public void cancelDismissControlViewTimer() {
        if (DISMISS_CONTROL_VIEW_TIMER != null) {
            DISMISS_CONTROL_VIEW_TIMER.cancel();
        }
        if (mDismissControlViewTimerTask != null) {
            mDismissControlViewTimerTask.cancel();
        }

    }

    @Override
    public void run() {
        mHandle.sendEmptyMessage(1);
    }
}
