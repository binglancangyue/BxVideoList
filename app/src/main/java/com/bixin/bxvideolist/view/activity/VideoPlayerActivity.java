package com.bixin.bxvideolist.view.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.bixin.bxvideolist.R;
import com.bixin.bxvideolist.adapter.RecyclerViewAdapter;
import com.bixin.bxvideolist.model.CustomValue;
import com.bixin.bxvideolist.model.bean.VideoBean;
import com.bixin.bxvideolist.model.bean.VideoPlayerBean;
import com.bixin.bxvideolist.model.listener.OnFinishVideoActivityListener;
import com.bixin.bxvideolist.model.listener.OnRecyclerViewItemListener;
import com.bixin.bxvideolist.model.tools.MyJzvdStd;
import com.bixin.bxvideolist.model.tools.ToastUtils;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.RxActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.jzvd.JzvdStd;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class VideoPlayerActivity extends RxActivity implements View.OnClickListener,
        OnRecyclerViewItemListener, XRecyclerView.LoadingListener, RecyclerViewAdapter.RecyclerViewOnItemListener, OnFinishVideoActivityListener {
    private final static String TAG = "VideoPlayerActivity";
    private Context mContext;
    private MyJzvdStd jcVideoPlayer;
    private CompositeDisposable compositeDisposable;
    private InnerHandler mInnerHandler;

    private List<VideoBean> normalVideoList = new ArrayList<>();
    private List<VideoBean> impactVideoList = new ArrayList<>();
    private List<VideoBean> videoList = new ArrayList<>();
    private int mCount = 12;
    public int mClickPosition = 0;

    private int type = 0;
    private RecyclerViewAdapter adapter;
    private XRecyclerView xRecyclerView;
    private TextView tvNormal;
    private TextView tvLock;
    private boolean isExit = false;

    @Override
    public void finishActivity() {
        finish();
    }

    private static class InnerHandler extends Handler {
        private final WeakReference<VideoPlayerActivity> activityWeakReference;

        private InnerHandler(VideoPlayerActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            VideoPlayerActivity activity = activityWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case CustomValue.UPDATE_BUTTON_BG:
                        activity.updateButtonBG();
                        break;
                    case CustomValue.HANDLE_EXIT_APP:
                        activity.isExit = false;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        this.mContext = this;
        compositeDisposable = new CompositeDisposable();
        mInnerHandler = new InnerHandler(this);
        initView();
        initData();
    }

    private void initView() {
//        xRecyclerView = findViewById(R.id.ryc_list);
        jcVideoPlayer = findViewById(R.id.jcv_player);
        jcVideoPlayer.setContext(mContext);
        jcVideoPlayer.setOnFinishVideoActivity(this);
//        tvNormal = findViewById(R.id.tv_btn_normal);
//        tvLock = findViewById(R.id.tv_btn_lock);
//        tvNormal.setOnClickListener(this);
//        tvLock.setOnClickListener(this);
//        updateButtonBG();
//        initRecyclerView();
    }

    private void initData() {
        Bundle bundle = this.getIntent().getExtras();
       /*
        if (bundle != null) {
            String path = bundle.getString(CustomValue.KEY_VIDEO_PATH);
            String name = bundle.getString(CustomValue.KEY_VIDEO_NAME);
            int index = bundle.getInt(CustomValue.KEY_VIDEO_INDEX);
            type = bundle.getInt(CustomValue.KEY_VIDEO_TYPE);
            playVideo(path, name);
            jcVideoPlayer.setIndex(index);
        }*/
        if (bundle != null) {
            VideoPlayerBean videoPlayerBean =
                    bundle.getParcelable("VideoPlayerBean");
            String path = videoPlayerBean.getPath();
            String name = videoPlayerBean.getName();
            int index = videoPlayerBean.getPosition();
            type = videoPlayerBean.getType();
            Log.d(TAG, "initData: " + path + " ");
            playVideo(path, name);
            jcVideoPlayer.setIndex(index);
        }
        geDataList();
        /*getWindow().getDecorView().post(() ->
                mInnerHandler.post(() -> {
                    //每次打开只加载30条数据
                    getVideoData(true);
                    //页面显示后后台异步加载全部数据
                    getVideoData(false);
                }));*/
    }

    @SuppressLint("CheckResult")
    private void getVideoData(boolean theFirst) {
        compositeDisposable.add(Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                geDataList();
                if (theFirst) {
                    setRecyclerViewAdapter();
                }
                emitter.onNext("ok");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY)).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (adapter == null) {
                            return;
                        }
                        if (!theFirst) {
                            if (videoList.size() == 0) {
                                return;
                            }
                            adapter.setData(videoList);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }));
    }

    /**
     * 获取本地视频
     */
    public void geDataList() {
        normalVideoList = MyApplication.getInstance().getNormalVideoList();
        impactVideoList = MyApplication.getInstance().getLockVideoList();
        setVideoListByType(type);
        jcVideoPlayer.setData(videoList);
        jcVideoPlayer.setAllData(normalVideoList, impactVideoList);
//        setRecyclerViewAdapter();
        Log.d(TAG, "geDataList:size " + normalVideoList.size() + " type: " + type);
    }

    /**
     * 根据type设置videoList的值
     *
     * @param type 视频类型
     */
    private void setVideoListByType(int type) {
        if (type == CustomValue.VIDEO_TYPE_NORMAL) {
            videoList = normalVideoList;
        } else {
            videoList = impactVideoList;
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        xRecyclerView.setLayoutManager(layoutManager);
        ((DefaultItemAnimator) Objects.requireNonNull(xRecyclerView.getItemAnimator()))
                .setSupportsChangeAnimations(false);
//        xRecyclerView.setPullRefreshEnabled(false);
        xRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(mContext).resumeRequests();//恢复Glide加载图片
                } else {
                    Glide.with(mContext).pauseRequests();//禁止Glide加载图片
                }
            }
        });
    }

    private void setRecyclerViewAdapter() {
        if (videoList.size() == 0) {
            Log.d(TAG, "setRecyclerViewAdapter:videoList null");
            return;
        }
        xRecyclerView.setLoadingListener(this);
        adapter = new RecyclerViewAdapter(mContext, videoList);
//        adapter.setOnRecyclerViewItemListener(this);
        adapter.setOnRecyclerViewItemListener(this);
        jcVideoPlayer.setData(videoList);
        DividerItemDecoration divider = new DividerItemDecoration(mContext,
                DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(mContext,
                R.drawable.transparent_dividing_verticall_line)));
        xRecyclerView.addItemDecoration(divider);
        xRecyclerView.setHasFixedSize(true);
        adapter.setHasStableIds(true);
        xRecyclerView.setItemViewCacheSize(20);
        xRecyclerView.setDrawingCacheEnabled(true);
        xRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        xRecyclerView.setAdapter(adapter);
    }

    private void playVideo(String videoPath, String name) {
        jcVideoPlayer.setUp(videoPath, name, JzvdStd.SCREEN_NORMAL);
        jcVideoPlayer.startVideo();
        Glide.with(mContext).load(videoPath).into(jcVideoPlayer.thumbImageView);
    }

    @Override
    public void onRefresh() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            SystemClock.sleep(1000);
            xRecyclerView.refreshComplete();
        }
    }

    @Override
    public void onLoadMore() {
        mCount += 10;
        adapter.setItemCount(mCount);
        xRecyclerView.loadMoreComplete();
    }

    @Override
    public void onClick(View view) {
        /*if (view.getId() == R.id.tv_btn_normal) {
            type = CustomValue.VIDEO_TYPE_NORMAL;
        } else {
            type = CustomValue.VIDEO_TYPE_LOCK;
        }*/
        setVideoListByType(type);
        mInnerHandler.sendEmptyMessage(CustomValue.UPDATE_BUTTON_BG);
        if (adapter == null) {
            return;
        }
        jcVideoPlayer.setData(videoList);
        adapter.setData(videoList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClickListener(int position) {
        this.mClickPosition = position;
        VideoBean videoBean = videoList.get(position);
        String name = videoBean.getName();
        String path = videoBean.getPath();
        jcVideoPlayer.setIndex(position);
        jcVideoPlayer.setUp(path, name, JzvdStd.SCREEN_NORMAL);
        if (name.contains("impact")) {
            jcVideoPlayer.type = CustomValue.VIDEO_TYPE_LOCK;
        } else {
            jcVideoPlayer.type = CustomValue.VIDEO_TYPE_NORMAL;
        }
        jcVideoPlayer.startVideo();
    }

    @Override
    public void onItemLongClickListener(int position) {

    }

    @Override
    public void onItemLongClickListener(int position, List<VideoBean> list) {

    }


    private void updateButtonBG() {
        if (type == CustomValue.VIDEO_TYPE_NORMAL) {
            tvNormal.setSelected(true);
            tvLock.setSelected(false);
        } else {
            tvNormal.setSelected(false);
            tvLock.setSelected(true);
        }
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            ToastUtils.showToast(R.string.exit_app);
            mInnerHandler.sendEmptyMessageDelayed(CustomValue.HANDLE_EXIT_APP, 2000);
        } else {
            finish();
//            System.exit(0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JzvdStd.releaseAllVideos();
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mInnerHandler != null) {
            mInnerHandler.removeCallbacksAndMessages(null);
            mInnerHandler = null;
        }
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
    }

    private class FinishReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

}
