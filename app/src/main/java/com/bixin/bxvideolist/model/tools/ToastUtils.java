package com.bixin.bxvideolist.model.tools;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bixin.bxvideolist.R;
import com.bixin.bxvideolist.view.activity.MyApplication;


public class ToastUtils {
    private static Toast toast = null;
    public static void showToast(int text) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.BOTTOM, 0, 50);

            LinearLayout linearLayout = (LinearLayout) toast.getView();
            linearLayout.setBackground(MyApplication.getInstance().getResources().getDrawable(R.drawable.circle_toast_bg));
            TextView messageTextView = (TextView) linearLayout.getChildAt(0);
            messageTextView.setTextSize(24);
            messageTextView.setTextColor(Color.WHITE);
            toast.setGravity(Gravity.BOTTOM, 0, 40);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }


/*    public static void showToast(int text) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_SHORT);
            LinearLayout linearLayout = (LinearLayout) toast.getView();
            TextView messageTextView = (TextView) linearLayout.getChildAt(0);
            if (CustomValue.IS_3IN) {
                messageTextView.setTextSize(24);
                toast.setGravity(Gravity.BOTTOM, 0, 50);
            }
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }*/
}
