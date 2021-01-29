package com.bixin.bxvideolist.model.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.bixin.bxvideolist.R;
import com.bixin.bxvideolist.model.CustomValue;
import com.bixin.bxvideolist.model.bean.VideoBean;
import com.bixin.bxvideolist.model.listener.OnFinishVideoActivityListener;
import com.bixin.bxvideolist.view.activity.VideoPlayerActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.jzvd.JzvdStd;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyJzvdStd extends JzvdStd {
    private ImageView ivNextBtn;
    private ImageView ivPreviousBtn;
    private ImageView ivHuaZhong;
    private List<VideoBean> mVideoBeanList;
    private int mCurrentVideoIndex = 0;
    private int maxLength;
    //    private int normalMaxLength = 0;
//    private int impactMaxLength = 0;
    private OnFinishVideoActivityListener activityListener;
    public int type = 0;
    private List<VideoBean> mNormalVideoList;
    private List<VideoBean> mImpactVideoList;
    private Context mContext;
    private LinearLayout mLinearLayout;
    private ImageView ivKd003Back;
    private VideoPlayerActivity mActivity;
    private boolean isHidepre = false;
    private boolean isHideNext = false;
    private MyHandle myHandle;


    public MyJzvdStd(Context context) {
        super(context);
    }

    public void setActivity(VideoPlayerActivity activity) {
        mActivity = activity;
    }

    public MyJzvdStd(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setContext(Context context) {
        this.mContext = context;
//        myHandle = new MyHandle(this);
    }

    public void setOnFinishVideoActivity(OnFinishVideoActivityListener onFinishVideoActivity) {
        this.activityListener = onFinishVideoActivity;
    }

    public void setVideoType(int number) {
        this.type = number;
        changeData();
    }

    public void setData(List<VideoBean> list) {
        mVideoBeanList = list;
        maxLength = mVideoBeanList.size() - 1;
       /* if (mCurrentVideoIndex == 0) {
            ivPreviousBtn.setVisibility(INVISIBLE);
        }
        if (list.size() <= 0) {
            ivNextBtn.setVisibility(INVISIBLE);
            ivPreviousBtn.setVisibility(INVISIBLE);
        }*/
    }

    public void setAllData(List<VideoBean> normalVideoList, List<VideoBean> impactVideoList) {
        this.mNormalVideoList = normalVideoList;
        this.mImpactVideoList = impactVideoList;
//        this.normalMaxLength = mNormalVideoList.size() - 1;
//        this.impactMaxLength = mImpactVideoList.size() - 1;
    }

    public void setIndex(int index) {
        this.mCurrentVideoIndex = index;
        Log.d(TAG, "setIndex:index " + index + " maxLength " + maxLength);
        if (index == 0) {
            isHidepre = true;
            ivPreviousBtn.setVisibility(View.INVISIBLE);
        }
        if (index == maxLength) {
            isHideNext = true;
            ivNextBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getLayoutId() {
        if (CustomValue.IS_KD003) {
            return R.layout.jz_layout_std_kd003;
        } else {
            return R.layout.jz_layout_std;
        }
    }

    @Override
    public void init(Context context) {
        super.init(context);
        mLinearLayout = findViewById(R.id.ll_status_bar);
        ivHuaZhong = findViewById(R.id.iv_huazhonghua);
        ivNextBtn = findViewById(R.id.next);
        ivNextBtn.setVisibility(VISIBLE);
        ivPreviousBtn = findViewById(R.id.previous);
//        ImageView ivScreen = findViewById(R.id.iv_screen_shot);
        backButton = findViewById(R.id.back);
//        backButton.setImageResource(R.drawable.jz_click_back_selector);
        backButton.setVisibility(VISIBLE);
        ivNextBtn.setOnClickListener(this);
        ivPreviousBtn.setOnClickListener(this);
//        fullscreenButton.setEnabled(false);
//        ivScreen.setOnClickListener(this);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick:back ");
                activityListener.finishActivity();
            }
        });

    }

    @Override
    public void setScreenNormal() {
        super.setScreenNormal();
        fullscreenButton.setImageResource(R.drawable.btn_zoom_out);
    }

    @Override
    public void setScreenFullscreen() {
        super.setScreenFullscreen();

        fullscreenButton.setImageResource(R.drawable.btn_zoom_out);
        backButton.setVisibility(View.GONE);
        batteryTimeLayout.setVisibility(View.GONE);
        changeStartButtonSize((int) getResources().getDimension(R.dimen.jz_start_button_w_h_fullscreen));
    }

   /* @Override
    public void setScreenFullscreen() {
        super.setScreenFullscreen();
        Jzvd.setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_ORIGINAL);
    }

    @Override
    public void setScreenNormal() {
        super.setScreenNormal();
        Jzvd.setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_ORIGINAL);
    }*/

    /**
     * 因为需要更改的start按钮的逻辑在基类Jzvd中,重写onclick,将父类Jzvd和JzvdStd的onclick逻辑都复制了过来
     * 并添加了next和previous按钮
     *
     * @param v 界面按钮
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
/*        if (i == R.id.start) {
            //每次点击图标打开app,当有数据时，默认播放第一个视频
            if (mVideoBeanList != null && mVideoBeanList.size() > 0 && mCurrentVideoIndex == 0
                    && state == STATE_IDLE) {
                setUp(mVideoBeanList.get(0).getPath(), mVideoBeanList.get(0).getName());
            }
        }

        //Jzvd
        if (i == R.id.start) {
            //每次点击图标打开app,当有数据时，默认播放第一个视频
            if (mVideoBeanList != null && mVideoBeanList.size() > 0 && mCurrentVideoIndex == 0
                    && state == STATE_IDLE) {
                setUp(mVideoBeanList.get(0).getPath(), mVideoBeanList.get(0).getName());
            }

            if (jzDataSource == null || jzDataSource.urlsMap.isEmpty()
                    || jzDataSource.getCurrentUrl() == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.no_url),
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: startno url");
                return;
            }
            if (state == STATE_NORMAL) {
                if (!jzDataSource.getCurrentUrl().toString().startsWith("file") && !
                        jzDataSource.getCurrentUrl().toString().startsWith("/") &&
                        !JZUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                    //这个可以放到std中
                    showWifiDialog();
                    return;
                }
                startVideo();
            } else if (state == STATE_PLAYING) {
                Log.d(TAG, "pauseVideo [" + this.hashCode() + "] ");
                mediaInterface.pause();
                onStatePause();
            } else if (state == STATE_PAUSE) {
                mediaInterface.start();
                onStatePlaying();
            } else if (state == STATE_AUTO_COMPLETE) {
                startVideo();
            }
        } else if (i == R.id.fullscreen) {
            Log.i(TAG, "onClick fullscreen [" + this.hashCode() + "] ");
            if (state == STATE_AUTO_COMPLETE) return;
            if (screen == SCREEN_FULLSCREEN) {
                //quit fullscreen
                backPress();
            } else {
                Log.d(TAG, "toFullscreenActivity [" + this.hashCode() + "] ");
                gotoScreenFullscreen();
            }
        }

        //by JzvdStd
        if (i == R.id.thumb) {
            if (jzDataSource == null || jzDataSource.urlsMap.isEmpty()
                    || jzDataSource.getCurrentUrl() == null) {
//                Toast.makeText(getContext(), getResources().getString(R.string.no_url),
//                        Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "onClick:thumb no_url");
                return;
            }
            if (state == STATE_NORMAL) {
                if (!jzDataSource.getCurrentUrl().toString().startsWith("file") &&
                        !jzDataSource.getCurrentUrl().toString().startsWith("/") &&
                        !JZUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                    showWifiDialog();
                    return;
                }
                startVideo();
            } else if (state == STATE_AUTO_COMPLETE) {
                onClickUiToggle();
            }
        } else if (i == R.id.surface_container) {
            startDismissControlViewTimer();
        } else if (i == R.id.back) {
            backPress();
        } else if (i == R.id.back_tiny) {
            clearFloatScreen();
        } else if (i == R.id.clarity) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout layout =
                    (LinearLayout) inflater.inflate(R.layout.jz_layout_clarity, null);

            OnClickListener mQualityListener = v1 -> {
                int index = (int) v1.getTag();
                changeUrl(index, getCurrentPositionWhenPlaying());
                clarity.setText(jzDataSource.getCurrentKey().toString());
                for (int j = 0; j < layout.getChildCount(); j++) {//设置点击之后的颜色
                    if (j == jzDataSource.currentUrlIndex) {
                        ((TextView) layout.getChildAt(j)).setTextColor(Color.parseColor(
                                "#fff85959"));
                    } else {
                        ((TextView) layout.getChildAt(j)).setTextColor(Color.parseColor("#ffffff"));
                    }
                }
                if (clarityPopWindow != null) {
                    clarityPopWindow.dismiss();
                }
            };

            for (int j = 0; j < jzDataSource.urlsMap.size(); j++) {
                String key = jzDataSource.getKeyFromDataSource(j);
                TextView clarityItem = (TextView) View.inflate(getContext(),
                        R.layout.jz_layout_clarity_item, null);
                clarityItem.setText(key);
                clarityItem.setTag(j);
                layout.addView(clarityItem, j);
                clarityItem.setOnClickListener(mQualityListener);
                if (j == jzDataSource.currentUrlIndex) {
                    clarityItem.setTextColor(Color.parseColor("#fff85959"));
                }
            }

            clarityPopWindow = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, true);
            clarityPopWindow.setContentView(layout);
            clarityPopWindow.showAsDropDown(clarity);
            layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int offsetX = clarity.getMeasuredWidth() / 3;
            int offsetY = clarity.getMeasuredHeight() / 3;
            clarityPopWindow.update(clarity, -offsetX, -offsetY,
                    Math.round(layout.getMeasuredWidth() * 2), layout.getMeasuredHeight());
        } else if (i == R.id.retry_btn) {
            if (jzDataSource.urlsMap.isEmpty() || jzDataSource.getCurrentUrl() == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.no_url),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (!jzDataSource.getCurrentUrl().toString().startsWith("file") && !
                    jzDataSource.getCurrentUrl().toString().startsWith("/") &&
                    !JZUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                showWifiDialog();
                return;
            }
            addTextureView();
            onStatePreparing();
        }*/
//        if (i == R.id.iv_screen_shot) {
//            aaaP();
//        }
//        changeData();
        if (i == R.id.previous || i == R.id.next) {
            // by altair,add next and previous button
            if (mVideoBeanList.size() == 0) {
                ToastUtils.showToast(R.string.not_video);
                return;
            }
            if (i == R.id.previous) {
//                quickRetreat();
                mCurrentVideoIndex--;
                /*if (mCurrentVideoIndex < 0) {
                    mCurrentVideoIndex = 0;
                    ToastUtils.showToast(R.string.already_first_video);
                    return;
                }*/
                Log.d(TAG, "onClick: mCurrentVideoIndex" + mCurrentVideoIndex);
                if (mCurrentVideoIndex <= 0) {
                    mCurrentVideoIndex = 0;
                    isHidepre = true;
                    ivPreviousBtn.setVisibility(View.INVISIBLE);
//                    myHandle.sendEmptyMessage(3);
//                    return;
                } else {
                    isHidepre = false;
                }
            }
            if (i == R.id.next) {
                mCurrentVideoIndex++;
//                if (mCurrentVideoIndex > maxLength) {
//                    mCurrentVideoIndex = maxLength;
//                    ToastUtils.showToast(R.string.already_last_video);
//                    return;
//                }
                Log.d(TAG, "onClick: mCurrentVideoIndex" + mCurrentVideoIndex +
                        " maxLength " + maxLength);
                if (mCurrentVideoIndex >= maxLength) {
                    mCurrentVideoIndex = maxLength;
                    isHideNext = true;
                    ivNextBtn.setVisibility(View.INVISIBLE);
//                    myHandle.sendEmptyMessage(1);
                } else {
                    isHideNext = false;
                }
            }
            if (mCurrentVideoIndex != 0) {
                isHidepre = false;
                ivPreviousBtn.setVisibility(View.VISIBLE);
//                myHandle.sendEmptyMessage(4);
            }
            if (mCurrentVideoIndex != maxLength) {
                isHideNext = false;
                ivNextBtn.setVisibility(View.VISIBLE);
//                myHandle.sendEmptyMessage(2);
            }
            setUp(mVideoBeanList.get(mCurrentVideoIndex).getPath(),
                    mVideoBeanList.get(mCurrentVideoIndex).getName());
            startVideo();
            gotoScreenFullscreen();
        }

    }

    /**
     * 根据最后一次点击列表item的type来切换数据
     */
    private void changeData() {
        if (type == CustomValue.VIDEO_TYPE_NORMAL) {
            mVideoBeanList = mNormalVideoList;
        } else {
            mVideoBeanList = mImpactVideoList;
        }
        if (mVideoBeanList == null) {
            return;
        }
        maxLength = mVideoBeanList.size() - 1;
    }

    /**
     * 添加Next和Previous按钮的隐藏显示逻辑
     *
     * @param topCon      topContainer
     * @param bottomCon   bottomContainer
     * @param startBtn    startButton
     * @param loadingPro  loadingProgressBar
     * @param thumbImg    thumbImageView
     * @param bottomPro   bottomProgressBar
     * @param retryLayout mRetryLayout
     */
    @Override
    public void setAllControlsVisiblity(int topCon, int bottomCon, int startBtn, int loadingPro,
                                        int thumbImg, int bottomPro, int retryLayout) {
        super.setAllControlsVisiblity(topCon, bottomCon, startBtn, loadingPro, thumbImg,
                bottomPro, retryLayout);
        //使用的start按钮的Visibility值
        if (isHidepre) {
            ivPreviousBtn.setVisibility(View.INVISIBLE);
        } else {
            ivPreviousBtn.setVisibility(startBtn);
        }
        if (isHideNext) {
            ivNextBtn.setVisibility(View.INVISIBLE);
        } else {
            ivNextBtn.setVisibility(startBtn);
        }

//        ivHuaZhong.setVisibility(startBtn);
        backButton.setVisibility(startBtn);
        mLinearLayout.setVisibility(startBtn);
        if (CustomValue.IS_KD003) {
//            fullscreenButton.setVisibility(startBtn);
        }
        if (mVideoBeanList == null) {
            return;
        }
        Log.d(TAG, "setAllControlsVisibility: " + mCurrentVideoIndex);

        /*//根据最后一次点击列表的type,来判断是否显示Next or Previous 按钮
        if (type == CustomValue.VIDEO_TYPE_NORMAL) {
            if (mCurrentVideoIndex >= normalMaxLength) {
                ivNextBtn.setVisibility(INVISIBLE);
            }
            if (mCurrentVideoIndex <= 0) {
                ivPreviousBtn.setVisibility(INVISIBLE);
            }
        } else {
            if (mCurrentVideoIndex >= impactMaxLength) {
                ivNextBtn.setVisibility(INVISIBLE);
            }
            if (mCurrentVideoIndex <= 0) {
                ivPreviousBtn.setVisibility(INVISIBLE);
            }
        }*/
    }

    @Override
    public void changeStartButtonSize(int size) {
        super.changeStartButtonSize(size);
        ViewGroup.LayoutParams layoutParams = ivPreviousBtn.getLayoutParams();
        layoutParams.height = size;
        layoutParams.width = size;
        layoutParams = ivNextBtn.getLayoutParams();
        layoutParams.height = size;
        layoutParams.width = size;
    }

    /**
     * 界面按钮几秒后自动掩藏
     */
    @Override
    public void dissmissControlView() {
//        super.dissmissControlView();
        if (state != STATE_NORMAL
                && state != STATE_ERROR
                && state != STATE_AUTO_COMPLETE) {
            post(() -> {
                backButton.setVisibility(INVISIBLE);
                ivHuaZhong.setVisibility(INVISIBLE);
                bottomContainer.setVisibility(View.INVISIBLE);
                topContainer.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.INVISIBLE);
                ivNextBtn.setVisibility(View.INVISIBLE);
                mLinearLayout.setVisibility(INVISIBLE);
                fullscreenButton.setVisibility(View.INVISIBLE);
                ivPreviousBtn.setVisibility(View.INVISIBLE);
                if (clarityPopWindow != null) {
                    clarityPopWindow.dismiss();
                }
                if (screen != SCREEN_TINY) {
                    bottomProgressBar.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    @Override
    public void onStateError() {
        super.onStateError();
        ToastUtils.showToast(R.string.video_playback_failed);
        if (activityListener != null) {
            activityListener.finishActivity();
        }
        Log.d(TAG, "onStateError: ");
    }

    @Override
    public void updateStartImage() {
        super.updateStartImage();
        Log.d(TAG, "updateStartImage: state " + state);
        if (state == STATE_ERROR) {
            ivNextBtn.setVisibility(View.GONE);
            ivPreviousBtn.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.GONE);
        }

        if (state == STATE_AUTO_COMPLETE) {
            activityListener.finishActivity();
        }

        if (state == STATE_PLAYING) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(R.drawable.btn_pause);
            replayTextView.setVisibility(GONE);
        } else if (state == STATE_PAUSE) {
            startButton.setImageResource(R.drawable.btn_play);
            replayTextView.setVisibility(GONE);
        }
    }


    public void doScreenshot(long time, ObservableEmitter<String> emitter) {
        String videoPath = mVideoBeanList.get(mCurrentVideoIndex).getPath();
        Log.d(TAG, "aaaP: video " + videoPath+" time "+time);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videoPath);
        Bitmap frameBitmap = mmr.getFrameAtTime(time * 1000);
        mmr.release();
        File file = new File(createPictureName());
        Log.d(TAG, "aaaP: filePath picture " + file.getAbsolutePath());
        try (FileOutputStream os = new FileOutputStream(file)) {
            frameBitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
            os.flush();
            os.close();
            emitter.onNext(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            emitter.onNext("fail");
        }
    }

    public long getTime() {
        return (progressBar.getProgress() * getDuration() / 100);
    }

    @SuppressLint("SimpleDateFormat")
    private String createPictureName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String name = dateFormat.format(date);
        String sdPath = StoragePaTool.getStoragePath(true);
        return sdPath + "/DVR-BX/Picture/" + name + "_screenshot" + ".jpg";
    }

    private void fastForward() {
        //总时间长度
        long duration = getDuration();
        //当前时间
        long currentPositionWhenPlaying = getCurrentPositionWhenPlaying();
        //快进（15S）
        long fastForwardProgress = currentPositionWhenPlaying + 10 * 1000;
        if (duration > fastForwardProgress) {
            mediaInterface.seekTo(fastForwardProgress);
        } else {
            mediaInterface.seekTo(duration);
        }
    }

    private void quickRetreat() { //当前时间
        long quickRetreatCurrentPositionWhenPlaying = getCurrentPositionWhenPlaying();
        //快退（15S）
        long quickRetreatProgress = quickRetreatCurrentPositionWhenPlaying - 10 * 1000;
        if (quickRetreatProgress > 0) {
            mediaInterface.seekTo(quickRetreatProgress);
        } else {
            mediaInterface.seekTo(0);
        }
    }

    private static class MyHandle extends Handler {
        private MyJzvdStd myJzvdStd;

        public MyHandle(MyJzvdStd jzvdStd) {
            WeakReference<MyJzvdStd> weakReference = new WeakReference<>(jzvdStd);
            myJzvdStd = weakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                myJzvdStd.setNextBtnShow(View.INVISIBLE);
            }
            if (msg.what == 2) {
                myJzvdStd.setNextBtnShow(View.VISIBLE);
            }
            if (msg.what == 3) {
                myJzvdStd.setPreviousBtnShow(View.INVISIBLE);
            }
            if (msg.what == 4) {
                myJzvdStd.setPreviousBtnShow(View.VISIBLE);
            }
            removeMessages(msg.what);
        }
    }

    private void setNextBtnShow(int isShow) {
        ivNextBtn.setVisibility(isShow);
    }

    private void setPreviousBtnShow(int isShow) {
        ivPreviousBtn.setVisibility(isShow);
    }

}
