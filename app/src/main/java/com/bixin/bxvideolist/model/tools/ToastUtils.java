package com.bixin.bxvideolist.model.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;


public class ToastUtils {
    private static Toast toast = null;

    @SuppressLint("ShowToast")
    public static void showToast(Context context, int text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);

        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
