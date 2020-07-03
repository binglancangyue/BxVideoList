package com.bixin.bxvideolist.model.tools;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;

import com.bixin.bxvideolist.R;
import com.bixin.bxvideolist.model.listener.OnDialogListener;
import com.bixin.bxvideolist.view.customview.CustomDialog;

import java.lang.ref.WeakReference;

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
    private CustomDialog mDeleteDialog;
    private CustomDialog mLockDialog;

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
            CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
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
                sendToActivity(0);
//            mInnerHandler.sendEmptyMessage(CustomValue.DELETE_FILE);
            });

            builder.setNegativeButton(R.string.button_cancel,
                    (dialog, which) -> dialog.dismiss());
            mDeleteDialog = builder.create();
        }
        if (!mDeleteDialog.isShowing()) {
            mDeleteDialog.show();
        }
    }

    /**
     * 显示加锁Dialog
     *
     * @param currentPage viewPager 的当前页数
     */
    public void showLockDialog(int currentPage) {
        if (mLockDialog == null) {
            CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
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
        if (!mLockDialog.isShowing()) {
            mLockDialog.show();
        }
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
            params.width = 500;
            mStopRecordingDialog.getWindow().setAttributes(params);
        }
        if (!mStopRecordingDialog.isShowing()){
            mStopRecordingDialog.show();
        }
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
            params.width = 500;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            loadingDialog.getWindow().setAttributes(params);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
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
}
