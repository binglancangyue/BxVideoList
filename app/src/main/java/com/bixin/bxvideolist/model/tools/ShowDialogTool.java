package com.bixin.bxvideolist.model.tools;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bixin.bxvideolist.R;
import com.bixin.bxvideolist.model.CustomValue;
import com.bixin.bxvideolist.model.listener.OnDialogListener;
import com.bixin.bxvideolist.view.customview.CustomDialog;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * @author Altair
 * @date :2019.11.22 下午 03:17
 * @description: 显示Dialog的工具类
 */
public class ShowDialogTool {
    private OnDialogListener mListener;
    private Context mContext;
    private ProgressDialog loadingDialog;
    private AlertDialog mStopRecordingDialog;
    private AlertDialog mDeleteDialog;
    private AlertDialog mLockDialog;

    public ShowDialogTool(OnDialogListener mListener, Context context) {
        this.mListener = mListener;
        WeakReference<Context> weakReference = new WeakReference<>(context);
        this.mContext = weakReference.get();
    }

    /**
     * 显示删除Dialog
     *
     * @param currentPage viewPager 的当前页数
     */
    public void showDeleteDialog(int currentPage) {
        if (mDeleteDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            if (currentPage == 2) {
                builder.setMessage(R.string.prompt_delete_picture);
            } else {
                builder.setMessage(R.string.prompt_delete_video);
            }
            builder.setTitle(R.string.dialog_title);
            builder.setPositiveButton(R.string.button_ok, (dialog, which) -> {
                dialog.dismiss();
                showWaitingDialog();
//            doLockFile(R.id.delete);
                sendToActivity(3);
//            mInnerHandler.sendEmptyMessage(CustomValue.DELETE_FILE);
            });

            builder.setNegativeButton(R.string.button_cancel,
                    (dialog, which) -> dialog.dismiss());
            mDeleteDialog = builder.create();
        }
        showDialog(mDeleteDialog);
    }

    /**
     * 显示加锁Dialog
     *
     * @param currentPage viewPager 的当前页数
     */
    public void showLockDialog(int currentPage) {
        if (mLockDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            if (currentPage == 0) {
                builder.setMessage(R.string.prompt_lock_video);
            } else {
                builder.setMessage(R.string.prompt_unlock_video);
            }
            builder.setTitle(R.string.dialog_title);
            builder.setPositiveButton(R.string.button_ok, (dialog, which) -> {
                dialog.dismiss();
                showWaitingDialog();
//            doLockFile(R.id.lock);
                sendToActivity(0);
            });

            builder.setNegativeButton(R.string.button_cancel,
                    (dialog, which) -> dialog.dismiss());
            mLockDialog = builder.create();
        }
        showDialog(mLockDialog);
    }

    /**
     * 显示是否停止录像的 Dialog
     */
    public void showStopRecordingDialog() {
        if (mStopRecordingDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.stop_dialog_title);
            builder.setMessage(mContext.getText(R.string.stop_recording));
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    mStopRecordingDialog.dismiss();
//                    finish();
                    sendToActivity(2);
                }
            });
            builder.setPositiveButton(R.string.btn_sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mStopRecordingDialog.dismiss();
//                    mViewPager.setVisibility(View.VISIBLE);
                    sendToActivity(1);
                }
            });
            builder.setNegativeButton(R.string.btn_cannel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mStopRecordingDialog.dismiss();
//                    finish();
                    sendToActivity(2);

                }
            });
            mStopRecordingDialog = builder.create();
            mStopRecordingDialog.setCanceledOnTouchOutside(false);
            WindowManager.LayoutParams params =
                    mStopRecordingDialog.getWindow().getAttributes();
            params.width = 450;
            mStopRecordingDialog.getWindow().setAttributes(params);
        }
        showDialog(mStopRecordingDialog);
    }

    /**
     * 显示正在等待的Dialog
     */
    public void showWaitingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(mContext, R.style.ProgressDialogStyle);
            loadingDialog.setMessage(mContext.getText(R.string.dialog_message));
            loadingDialog.setIndeterminate(true);
            loadingDialog.setCancelable(false);//点击返回或四周是否关闭dialog true关闭 false不可关闭
            loadingDialog.setOnKeyListener((dialog, keyCode, event) -> false);
            WindowManager.LayoutParams params =
                    loadingDialog.getWindow().getAttributes();
            params.width = 450;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            loadingDialog.getWindow().setAttributes(params);
        }
        showDialog(loadingDialog);
    }

    private void sendToActivity(int type) {
        if (mListener != null) {
            mListener.doSomething(type);
        }
    }

    public void loadingDialogDismiss() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    public void dismissDialog() {
        loadingDialogDismiss();
        if (mStopRecordingDialog != null) {
            mStopRecordingDialog.dismiss();
        }
        if (mDeleteDialog != null) {
            mDeleteDialog.dismiss();
        }
        if (mLockDialog != null) {
            mLockDialog.dismiss();
        }
    }

    private void showDialog(AlertDialog alertDialog) {
        if (!alertDialog.isShowing()) {
            alertDialog.show();
            if (CustomValue.IS_3IN) {
                setDialogTextSize(alertDialog);
            }
        }
    }

    private void setDialogTextSize(AlertDialog builder) {
//        builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(27);
//        builder.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(27);
        Button button_negative = builder.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button button_positive = builder.getButton(AlertDialog.BUTTON_POSITIVE);
        button_negative.setTextSize(27);
        button_positive.setTextSize(27);
        builder.getWindow().setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button_positive.getLayoutParams();
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) button_positive.getLayoutParams();
        layoutParams.height = 80;
        layoutParams.width = 90;
        layoutParams.setMargins(0, 0, 5, 0);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams1.gravity = Gravity.CENTER;
        layoutParams1.height = 80;
        layoutParams1.width = 90;
        button_negative.setLayoutParams(layoutParams);
        button_positive.setLayoutParams(layoutParams1);
        try {
            //获取mAlert对象
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(builder);

            //获取mTitleView并设置大小颜色
            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mAlertController);
            if (mTitleView != null) {
                mTitleView.setTextSize(30);
            }

            //获取mMessageView并设置大小颜色
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            if (mMessageView != null) {
                mMessageView.setTextSize(27);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
