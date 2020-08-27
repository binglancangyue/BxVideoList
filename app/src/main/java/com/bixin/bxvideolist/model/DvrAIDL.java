package com.bixin.bxvideolist.model;

import android.content.Context;
import android.util.Log;

import com.bixin.bxvideolist.model.tools.ShowDialogTool;
import com.bixin.bxvideolist.view.activity.MyApplication;

/**
 * @author Altair
 * @date :2019.12.31 上午 11:17
 * @description:
 */
public class DvrAIDL {
    private Context mContext;
    private boolean isRecording = false;
     private ShowDialogTool mDialogTool;

    public DvrAIDL() {
        this.mContext = MyApplication.getInstance();
    }



    public boolean isRecording() {
        Log.d("DVRAIDL", "isRecording: ");
        return isRecording;
    }
}
