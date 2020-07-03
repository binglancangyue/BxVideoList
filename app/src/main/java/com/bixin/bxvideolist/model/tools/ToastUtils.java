package com.bixin.bxvideolist.model.tools;

import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bixin.bxvideolist.view.activity.MyApplication;


public class ToastUtils {
    private static Toast toast = null;

    public static void showToast(int text) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 50);

            LinearLayout linearLayout = (LinearLayout) toast.getView();
            TextView messageTextView = (TextView) linearLayout.getChildAt(0);
            messageTextView.setTextSize(24);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
