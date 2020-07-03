/*
package com.bixin.bxvideolist.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.bixin.bxvideolist.R;
import com.bixin.bxvideolist.adapter.RecyclerViewAdapter;
import com.bixin.bxvideolist.adapter.ViewPageAdapter;
import com.bixin.bxvideolist.model.CustomValue;
import com.bixin.bxvideolist.model.DvrAIDL;
import com.bixin.bxvideolist.model.bean.VideoBean;
import com.bixin.bxvideolist.model.listener.OnDialogListener;
import com.bixin.bxvideolist.model.tools.MediaData;
import com.bixin.bxvideolist.model.tools.ShowDialogTool;
import com.bixin.bxvideolist.model.tools.VideoListOperationTool;
import com.bixin.bxvideolist.view.customview.CustomRecyclerView;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.RxActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends RxActivity implements View.OnClickListener,
        RecyclerViewAdapter.RecyclerViewOnItemListener, OnDialogListener {
    private static final String TAG = "MainActivity";
    private Context mContext;
    private ViewPager mViewPager;
    private List<VideoBean> normalVideoList;
    private List<VideoBean> impactVideoList;
    private List<VideoBean> pictureList;
    private CustomRecyclerView normalVideoRecyclerView, lockVideoRecyclerView, pictureRecyclerView;
    private LinearLayout ctlNormalVideo;
    private LinearLayout ctlPicture;
    private LinearLayout ctlLockVideo;
    private int currentPage;
    private ViewPageAdapter adapter;
    public List<View> viewList = new ArrayList<>();
    private RecyclerViewAdapter normalVideoAdapter;
    private RecyclerViewAdapter lockVideoAdapter;
    private RecyclerViewAdapter pictureAdapter;
    private final static int CTR_DELETE = 0;
    private final static int CTR_LOCK = 1;
    private final static int CTR_UNLOCK = 2;
    //    private CustomDialog loadingDialog;
    private ShowDialogTool mDialogTool;
    private VideoListOperationTool mFileTool;

    private InnerHandler mInnerHandler;

    private ProgressDialog loadingDialog;
    private AlertDialog stopDialog;

    private int normalItemCount = 12;
    private int lockItemCount = 12;
    private int pictureItemCount = 12;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MediaData mediaData;

    @Override
    public void doSomething(int type) {
        if (type == 0) {
            doLockFile(R.id.lock);
        } else if (type == 1) {
            mViewPager.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }

    private static class InnerHandler extends Handler {
        private final WeakReference<MainActivity> activityWeakReference;

        private InnerHandler(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case CustomValue.LOCK_FILE:
//                        activity.lockFile(R.id.lock);
                        activity.toLockFile(R.id.lock);
                        break;
                    case CustomValue.DELETE_FILE:
//                        activity.lockFile(R.id.delete);
                        activity.toLockFile(R.id.delete);
                        break;
                    case CustomValue.CLOSE_DIALOG:
                        activity.loadingDialogDismiss();
                        break;
                    case 6:
                        */
/*if (msg.arg1 == 0) {
                            activity.fileManagement(activity.normalVideoList,
                                    activity.impactVideoList);
                        }
                        if (msg.arg1 == 1) {
                            activity.fileManagement(activity.impactVideoList,
                                    activity.normalVideoList);
                        }
                        if (msg.arg1 == 2) {
                            activity.fileManagement(activity.pictureList, activity.pictureList);
                        }
                        activity.normalVideoAdapter.notifyDataSetChanged();
                        activity.lockVideoAdapter.notifyDataSetChanged();
                        activity.pictureAdapter.notifyDataSetChanged();*//*

                        break;
                    case 7:
                        activity.normalVideoRecyclerView.setHasButton(true);

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
        setContentView(R.layout.activity_main);
        this.mContext = this;
        init();
        initView();
        initData();
//        requestPermissions(MainActivity.this);

//        doGetVideoData(false);
    }

    private void initData() {
        mDialogTool.showStopRecordingDialog();
//        showWaitingDialog(R.string.loading_data);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mInnerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //每次打开应用只显示50数据
                        doGetVideoData(true);
                        //之后再加载全部数据
                        doGetVideoData(false);
                    }
                });
            }
        });
    }

    @SuppressLint("CheckResult")
    private void doGetVideoData(boolean theFirst) {
        compositeDisposable.add(Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                getData(theFirst);
                emitter.onNext("ok");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (s.equals("ok")) {
                            if (theFirst) {
                                initPagerItemView();
                                adapter = new ViewPageAdapter(viewList);
                                mViewPager.setAdapter(adapter);
//                        stopDialog.dismiss();
//                        loadingDialog.dismiss();
                            } else {
                                if (normalVideoAdapter == null) {
                                    Log.d(TAG, "accept: normalVideoAdapter null");
                                    return;
                                }
                                adapterSetData();
                            }
                        }
                    }
                }));
    }

    private void adapterSetData() {
        normalVideoAdapter.setData(normalVideoList);
        lockVideoAdapter.setData(impactVideoList);
        pictureAdapter.setData(pictureList);
    }

    */
/**
     * 获取本地数据
     *//*

    private void getData(boolean theFirst) {
        if (mediaData == null) {
            mediaData = new MediaData();
        }
        mediaData.rescan(theFirst);
        List<VideoBean> videoBeans = new ArrayList<>();
        videoBeans.add(new VideoBean("asd", "aad", "adf", false, 2));
        videoBeans.add(new VideoBean("as12d", "aad", "adf", false, 2));
        normalVideoList = videoBeans;
        impactVideoList = mediaData.getImpactVideoList();
        pictureList = mediaData.getPictureList();
    }

    private void init() {
        this.mContext = this;
        mInnerHandler = new InnerHandler(this);
        mDialogTool = new ShowDialogTool(this, this);
        mFileTool = new VideoListOperationTool();
//        compositeDisposable = new CompositeDisposable();
        DvrAIDL dvrAIDL = new DvrAIDL();
        dvrAIDL.bindAIDLService();
    }

    private void initView() {
        mViewPager = findViewById(R.id.viewpager);
        ctlNormalVideo = findViewById(R.id.ctl_normal);
        ctlLockVideo = findViewById(R.id.ctl_lock);
        ctlPicture = findViewById(R.id.ctl_picture);
        ctlNormalVideo.setOnClickListener(myOnClickListener);
        ctlLockVideo.setOnClickListener(myOnClickListener);
        ctlPicture.setOnClickListener(myOnClickListener);
//        mViewPager.setOffscreenPageLimit(2);

        mViewPager.setCurrentItem(0);
        mViewPager.setVisibility(View.INVISIBLE);
        changeColor(0);
        setOnPageChangeListener();
    }

    private void initPagerItemView() {
        normalVideoRecyclerView = new CustomRecyclerView(this, null, 0);
        lockVideoRecyclerView = new CustomRecyclerView(this, null, 1);
        pictureRecyclerView = new CustomRecyclerView(this, null, 2);

        normalVideoAdapter = new RecyclerViewAdapter(this, normalVideoList);
        lockVideoAdapter = new RecyclerViewAdapter(this, impactVideoList);
        pictureAdapter = new RecyclerViewAdapter(this, pictureList);

        normalVideoAdapter.setOnRecyclerViewItemListener(this);
        lockVideoAdapter.setOnRecyclerViewItemListener(this);
        pictureAdapter.setOnRecyclerViewItemListener(this);

        normalVideoAdapter.setHasStableIds(true);
        lockVideoAdapter.setHasStableIds(true);
        pictureAdapter.setHasStableIds(true);

        normalVideoRecyclerView.setAdapter(normalVideoAdapter);
        lockVideoRecyclerView.setAdapter(lockVideoAdapter);
        pictureRecyclerView.setAdapter(pictureAdapter);

        setRecyclerViewLoadingListener();
        setRecyclerViewScrollListener();

        normalVideoRecyclerView.setButtonOnClickListener(this);
        lockVideoRecyclerView.setButtonOnClickListener(this);
        pictureRecyclerView.setButtonOnClickListener(this);

        viewList.add(normalVideoRecyclerView);
        viewList.add(lockVideoRecyclerView);
        viewList.add(pictureRecyclerView);
    }


    private void loadingDialogDismiss() {
        mDialogTool.loadingDialogDismiss();
    }

    private void toLockFile(int id) {
        mFileTool.lockFile(id, currentPage, normalVideoList, impactVideoList,
                pictureList);
    }

    */
/**
     * xRecyclerView LoadingListener
     *//*

    private void setRecyclerViewLoadingListener() {
        normalVideoRecyclerView.xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                normalItemCount += 10;
                normalVideoAdapter.setItemCount(normalItemCount);
                normalVideoRecyclerView.xRecyclerView.loadMoreComplete();
            }
        });

        lockVideoRecyclerView.xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                lockItemCount += 10;
                lockVideoAdapter.setItemCount(lockItemCount);
                lockVideoRecyclerView.xRecyclerView.loadMoreComplete();
            }
        });
        pictureRecyclerView.xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                pictureItemCount += 10;
                pictureAdapter.setItemCount(pictureItemCount);
                pictureRecyclerView.xRecyclerView.loadMoreComplete();
            }
        });
    }

    */
/**
     * recyclerView ScrollListener 滑动不加载图片
     *//*

    private void setRecyclerViewScrollListener() {
        normalVideoRecyclerView.xRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                glideLoadPictureControl(newState);
            }
        });
        lockVideoRecyclerView.xRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                glideLoadPictureControl(newState);
            }
        });
        pictureRecyclerView.xRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                glideLoadPictureControl(newState);
            }
        });
    }

    */
/**
     * 根据recyclerView scroll状态判断是否加载图片
     *
     * @param newState 状态值
     *//*

    private void glideLoadPictureControl(int newState) {
        if (this.isFinishing()) {
            return;
        }
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            Glide.with(mContext).resumeRequests();//恢复Glide加载图片
        } else {
            Glide.with(mContext).pauseRequests();//禁止Glide加载图片
        }
    }

    */
/**
     * item 长按显示的4个按钮的onClick
     *
     * @param v
     *//*

    @Override
    public void onClick(View v) {
        List<VideoBean> mList = null;
        RecyclerViewAdapter adapter = null;
        CustomRecyclerView view = null;
        switch (currentPage) {
            case 0:
                mList = normalVideoList;
                adapter = normalVideoAdapter;
                view = normalVideoRecyclerView;
                break;
            case 1:
                mList = impactVideoList;
                adapter = lockVideoAdapter;
                view = lockVideoRecyclerView;
                break;
            case 2:
                mList = pictureList;
                adapter = pictureAdapter;
                view = pictureRecyclerView;
                break;
        }
        switch (v.getId()) {
            case R.id.select_all:
                if (mList == null) {
                    return;
                }
//                for (int i = 0; i < mList.size(); i++) {
//                    mList.get(i).setSelect(true);
//                }
                for (VideoBean videoBean : mList) {
                    videoBean.setSelect(true);
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.lock:
                mDialogTool.showLockDialog(currentPage);
                break;
            case R.id.delete:
                mDialogTool.showDeleteDialog(currentPage);
                break;
            case R.id.cancel:
                if (mList == null) {
                    return;
                }
//                for (int i = 0; i < mList.size(); i++) {
//                    mList.get(i).setSelect(false);
//                }
                for (VideoBean videoBean : mList) {
                    videoBean.setSelect(false);
                }
                if (adapter != null) {
                    view.setHasButton(false);
                    adapter.setIsShowSelect(false);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    */
/**
     * viewPager 监听
     *//*

    private void setOnPageChangeListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                changeColor(currentPage);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    */
/**
     * recyclerView itemClickListener
     *
     * @param position 下标
     *//*

    @Override
    public void onItemClickListener(int position) {
        VideoBean data = null;
        RecyclerViewAdapter adapter = null;
        switch (currentPage) {
            case 0:
                Log.d(TAG, "onItemClickListener:normalVideoList " + normalVideoList.size());
                data = normalVideoList.get(position);
                adapter = normalVideoAdapter;
                break;
            case 1:
                data = impactVideoList.get(position);
                adapter = lockVideoAdapter;
                break;
            case 2:
                data = pictureList.get(position);
                adapter = pictureAdapter;
                break;
        }
        if (adapter.getIsShowSelect()) {
            assert data != null;
            if (data.getSelect()) {
                data.setSelect(false);
            } else {
                data.setSelect(true);
            }
            adapter.notifyItemChanged(position + 1);
        } else {
            if (currentPage != 2) {
                mFileTool.openVideoPlayer(data, position, currentPage, this);
            } else {
                ArrayList<String> picPaths = new ArrayList<>();
                for (VideoBean videoBean : pictureList) {
                    picPaths.add(videoBean.getPath());
                }
                Intent intent = new Intent(MainActivity.this, ShowPictureActivity.class);
                intent.putExtra("item", position);
                intent.putStringArrayListExtra("picPaths", picPaths);
                startActivity(intent);
            }
        }
    }

    */
/**
     * recyclerView itemLongClickListener
     *
     * @param position 下标,回传时+1
     *//*

    @Override
    public void onItemLongClickListener(int position) {
        switch (currentPage) {
            case 0:
                if (!normalVideoAdapter.getIsShowSelect()) {
                    normalVideoAdapter.setIsShowSelect(true);
                    normalVideoRecyclerView.setHasButton(true);
                    normalVideoList.get(position).setSelect(true);
                }
                normalVideoAdapter.notifyItemChanged(position + 1);
                break;
            case 1:
                if (!lockVideoAdapter.getIsShowSelect()) {
                    lockVideoAdapter.setIsShowSelect(true);
                    lockVideoRecyclerView.setHasButton(true);
                    impactVideoList.get(position).setSelect(true);
                }
                lockVideoAdapter.notifyItemChanged(position + 1);
                break;
            case 2:
                if (!pictureAdapter.getIsShowSelect()) {
                    pictureAdapter.setIsShowSelect(true);
                    pictureRecyclerView.setHasButton(true);
                    pictureList.get(position).setSelect(true);
                }
                pictureAdapter.notifyItemChanged(position + 1);
                break;
        }
    }

    */
/**
     * 3个导航按钮点击事件
     *//*

    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ctl_normal:
                    mViewPager.setCurrentItem(0);
                    changeColor(0);
                    break;
                case R.id.ctl_lock:
                    mViewPager.setCurrentItem(1);
                    changeColor(1);
                    break;
                case R.id.ctl_picture:
                    mViewPager.setCurrentItem(2);
                    changeColor(2);
                    break;
            }
        }
    };

    private void clearAll() {
        ctlNormalVideo.setSelected(false);
        ctlLockVideo.setSelected(false);
        ctlPicture.setSelected(false);
    }

    */
/**
     * 改变被点击导航按钮颜色
     *
     * @param position viewpager position
     *//*

    private void changeColor(int position) {
        clearAll();
        switch (position) {
            case 0:
                ctlNormalVideo.setSelected(true);
                currentPage = 0;
                break;
            case 1:
                ctlLockVideo.setSelected(true);
                currentPage = 1;
                break;
            case 2:
                ctlPicture.setSelected(true);
                currentPage = 2;
                break;
        }
    }


    private void doLockFile(int btnId) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                toLockFile(btnId);
                emitter.onNext(true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        normalVideoAdapter.notifyDataSetChanged();
                        lockVideoAdapter.notifyDataSetChanged();
                        pictureAdapter.notifyDataSetChanged();
                        loadingDialogDismiss();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @SuppressLint("CheckResult")
    public void requestPermissions(Activity activity) {

        String[] strings = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        RxPermissions rxPermission = new RxPermissions(activity);
        rxPermission.requestEach(strings)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        if (permission.granted) {// 用户已经同意该权限
                            initData();
                            Log.d(TAG, permission.name + " is granted.");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.d(TAG, permission.name + " is denied. More info should be " +
                                    "provided.");
                            finish();
                        } else { // 用户拒绝了该权限，并且选中『不再询问』
                            finish();
                            Log.d(TAG, permission.name + " is denied.");
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInnerHandler.removeCallbacksAndMessages(null);
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
    }
}
*/
