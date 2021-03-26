package com.bixin.bxvideolist.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bixin.bxvideolist.R;
import com.bixin.bxvideolist.adapter.HomeRecyclerViewAdapter;
import com.bixin.bxvideolist.adapter.ViewPageAdapter;
import com.bixin.bxvideolist.model.CustomValue;
import com.bixin.bxvideolist.model.DvrAIDL;
import com.bixin.bxvideolist.model.bean.VideoBean;
import com.bixin.bxvideolist.model.listener.OnDialogListener;
import com.bixin.bxvideolist.model.listener.OnRecyclerViewItemListener;
import com.bixin.bxvideolist.model.tools.MediaData;
import com.bixin.bxvideolist.model.tools.ShowDialogTool;
import com.bixin.bxvideolist.model.tools.ToastUtils;
import com.bixin.bxvideolist.model.tools.VideoListOperationTool;
import com.bixin.bxvideolist.view.customview.CustomRecyclerView;
import com.bx.carDVR.bylym.myaidl.FileListInterface;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.RxActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class HomeActivity extends RxActivity implements View.OnClickListener,
        OnRecyclerViewItemListener, OnDialogListener {
    private static final String TAG = "HomeActivity";
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
    private HomeRecyclerViewAdapter normalVideoAdapter;
    private HomeRecyclerViewAdapter lockVideoAdapter;
    private HomeRecyclerViewAdapter pictureAdapter;

    private InnerHandler mInnerHandler;
    private CompositeDisposable compositeDisposable;
    private MediaData mediaData;
    private ShowDialogTool mDialogTool;
    private VideoListOperationTool mFileTool;

    private ImageView ivLockVideo;
    private ImageView ivPhoto;
    private ImageView ivNormalVideo;
    private boolean isExit = false;
    private boolean isNotShowDialog = true;
    private DvrAIDL dvrAIDL;
    private FileListInterface listInterface;

    @Override
    public void doSomething(int type) {
        if (type == 0) {
            doLockFile(R.id.lock);
        } else if (type == 1) {
            sendBroadcastForStopRecording();
            mViewPager.setVisibility(View.VISIBLE);
        } else if (type == 2) {
            finish();
        } else {
            doLockFile(R.id.delete);
        }
    }

    private void sendBroadcastForStopRecording() {
        Intent intent = new Intent();
        intent.setAction(CustomValue.ACTION_STOP_RECORD);
        sendBroadcast(intent);
    }

    private static class InnerHandler extends Handler {
        private final WeakReference<HomeActivity> activityWeakReference;

        private InnerHandler(HomeActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HomeActivity activity = activityWeakReference.get();
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
                        activity.pictureAdapter.notifyDataSetChanged();*/
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

    private void loadingDialogDismiss() {
        mDialogTool.loadingDialogDismiss();
    }

    private void toLockFile(int id) {
        mFileTool.lockFile(id, currentPage, normalVideoList, impactVideoList,
                pictureList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarVisible(false);
        super.onCreate(savedInstanceState);
        View view;
        if (CustomValue.IS_KD003) {
            view = getLayoutInflater().inflate(R.layout.activity_main_kd003, null);
        } else {
            view = getLayoutInflater().inflate(R.layout.activity_main, null);
        }
        setContentView(view);
        init();
        initView();
//        initData();
//        requestPermissions(HomeActivity.this);
        initData();
    }

    private void setStatusBarVisible(boolean show) {
        if (!CustomValue.IS_966) {
            return;
        }
        if (show) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    private void get(Map<String, List<String>> map) {
        List<VideoBean> list = new ArrayList<>();
        Iterator<Map.Entry<String, List<String>>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<String>> entry = entries.next();
            List<String> stringList = entry.getValue();
            Log.d(TAG, "get:name  " + entry.getKey());
            for (String s : stringList) {
                VideoBean videoBean = new VideoBean();
                videoBean.setName(new File(s).getName());
                videoBean.setPath(s);
                Log.d(TAG, "get: path " + s);
                list.add(videoBean);
            }
        }
        pictureList = list;
    }

    private void init() {
        this.mContext = this;
        mInnerHandler = new InnerHandler(this);
        mDialogTool = new ShowDialogTool(this, this);
        mFileTool = new VideoListOperationTool();
        compositeDisposable = new CompositeDisposable();
        if (CustomValue.IS_3IN) {
            bindAIDLService();
        }
    }

    private void initData() {
        isShowStopRecordingDialog();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mInnerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //每次打开应用只显示50数据
                        doGetVideoData(true);
//                        bindAIDLService();
                        //之后再加载全部数据
                        doGetVideoData(false);
                    }
                });
            }
        });
    }

    private void initDataTest() {
        mDialogTool.showStopRecordingDialog();
        doGetVideoData(true);
    }


    @SuppressLint("CheckResult")
    private void doGetVideoData(Map<String, List<String>> map) {
        compositeDisposable.add(Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                getData(true);
//                get(map);
                emitter.onNext("ok");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (s.equals("ok")) {
                            initPagerItemView();
                            adapter = new ViewPageAdapter(viewList);
                            mViewPager.setAdapter(adapter);
                        }
                    }
                }));
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

    /**
     * 获取本地数据
     */
    private void getData(boolean theFirst) {
        if (mediaData == null) {
            mediaData = new MediaData();
        }
        mediaData.rescan(theFirst);
//        mediaData.rescan();
//        List<VideoBean> videoBeans=new ArrayList<>();
//        videoBeans.add(new VideoBean("asd",))
        normalVideoList = mediaData.getNormalVideoList();
        impactVideoList = mediaData.getImpactVideoList();
        pictureList = mediaData.getPictureList();
        MyApplication.getInstance().setNormalVideoList(normalVideoList);
        MyApplication.getInstance().setLockVideoList(impactVideoList);
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
        if (CustomValue.IS_KD003) {
            ivNormalVideo = findViewById(R.id.iv_normal);
            ivLockVideo = findViewById(R.id.iv_lock);
            ivPhoto = findViewById(R.id.iv_picture);
            ivLockVideo.setImageResource(R.drawable.icon_home_cloud_video);
            ivPhoto.setImageResource(R.drawable.icon_home_photo);
        }

        mViewPager.setCurrentItem(0);
        mViewPager.setVisibility(View.INVISIBLE);
        changeColor(0);
        setOnPageChangeListener();
    }

    private void initPagerItemView() {
        normalVideoRecyclerView = new CustomRecyclerView(this, null, 0);
        lockVideoRecyclerView = new CustomRecyclerView(this, null, 1);
        pictureRecyclerView = new CustomRecyclerView(this, null, 2);

        normalVideoAdapter = new HomeRecyclerViewAdapter(this, normalVideoList);
        lockVideoAdapter = new HomeRecyclerViewAdapter(this, impactVideoList);
        pictureAdapter = new HomeRecyclerViewAdapter(this, pictureList);

        normalVideoAdapter.setOnRecyclerViewItemListener(this);
        lockVideoAdapter.setOnRecyclerViewItemListener(this);
        pictureAdapter.setOnRecyclerViewItemListener(this);

        normalVideoAdapter.setHasStableIds(true);
        lockVideoAdapter.setHasStableIds(true);
        pictureAdapter.setHasStableIds(true);

        normalVideoRecyclerView.setAdapter(normalVideoAdapter);
        lockVideoRecyclerView.setAdapter(lockVideoAdapter);
        pictureRecyclerView.setAdapter(pictureAdapter);

//        setRecyclerViewLoadingListener();
//        setRecyclerViewScrollListener();
        normalVideoRecyclerView.setButtonOnClickListener(this);
        lockVideoRecyclerView.setButtonOnClickListener(this);
        pictureRecyclerView.setButtonOnClickListener(this);

        viewList.add(normalVideoRecyclerView);
        viewList.add(lockVideoRecyclerView);
        viewList.add(pictureRecyclerView);
    }

    /**
     * item 长按显示的4个按钮的onClick
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        List<VideoBean> mList = null;
        HomeRecyclerViewAdapter adapter = null;
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
                for (VideoBean videoBean : mList) {
                    videoBean.setSelect(true);
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.lock:
                mDialogTool.showLockDialog(currentPage);
//                showLockDialog();
                break;
            case R.id.delete:
                mDialogTool.showDeleteDialog(currentPage);
                break;
            case R.id.cancel:
                if (mList == null) {
                    return;
                }
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

    /**
     * viewPager 监听
     */
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

    /**
     * recyclerView itemClickListener
     *
     * @param position 下标
     */
    @Override
    public void onItemClickListener(int position) {

        VideoBean data = null;
        HomeRecyclerViewAdapter adapter = null;
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
                isNotShowDialog = false;
                Intent intent = mFileTool.openVideoPlayer(data, position, currentPage);
                startActivityForResult(intent, 10086);
            } else {
                ArrayList<String> picPaths = new ArrayList<>();
                for (VideoBean videoBean : pictureList) {
                    picPaths.add(videoBean.getPath());
                }
                isNotShowDialog = false;
                Intent intent = new Intent(HomeActivity.this, ShowPictureActivity.class);
                intent.putExtra("item", position);
                intent.putStringArrayListExtra("picPaths", picPaths);
                startActivityForResult(intent, 10086);
            }
        }
    }

    /**
     * recyclerView itemLongClickListener
     *
     * @param position 下标,回传时+1
     */
    @Override
    public void onItemLongClickListener(int position, List<VideoBean> list) {
        Log.d(TAG, "onItemLongClickListener:currentPage " + currentPage + " position " + position);
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

    /**
     * 3个导航按钮点击事件
     */
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

    /**
     * 改变被点击导航按钮颜色
     *
     * @param position viewpager position
     */
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
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        List<String> stringList = new ArrayList<>();
        for (String s : permissions) {
            if (ActivityCompat.checkSelfPermission(this, s)
                    != PackageManager.PERMISSION_GRANTED) {
                stringList.add(s);
            }
        }
        if (!stringList.isEmpty()) {
            RxPermissions rxPermission = new RxPermissions(activity);
            rxPermission.requestEach(permissions)
                    .subscribe(new Consumer<Permission>() {
                        @Override
                        public void accept(Permission permission) {
                            if (permission.granted) {// 用户已经同意该权限
//                            initData();
                                initDataTest();
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
        } else {
            initData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086) {
            Log.d(TAG, "onActivityResult: ");
            isNotShowDialog = true;
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

    private void exit() {
        if (!isExit) {
            isExit = true;
            ToastUtils.showToast(R.string.exit_app);
            mInnerHandler.sendEmptyMessageDelayed(CustomValue.HANDLE_EXIT_APP, 2000);
        } else {
            System.exit(0);
        }
    }


    public void bindAIDLService() {
        Intent intent = new Intent();
        intent.setPackage("com.bx.carDVR");
        intent.setAction("com.bx.carDVR.aidl_service");
        mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBingAIDLService() {
        mContext.unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            listInterface = FileListInterface.Stub.asInterface(service);
            if (listInterface == null) {
                Log.d(TAG, "onServiceConnected:null");
                return;
            }
            try {
                Log.d(TAG, "onServiceConnected: " + listInterface.isRecording());
                if (listInterface.isRecording()) {
                    mDialogTool.showStopRecordingDialog();
                } else {
                    mViewPager.setVisibility(View.VISIBLE);
                }
            } catch (RemoteException e) {
                Log.e(TAG, "onServiceConnected: " + e.getLocalizedMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
        }

        @Override
        public void onNullBinding(ComponentName name) {
            Log.d(TAG, "onNullBinding: ");
        }

    };

    private void isShowStopRecordingDialog() {
        int state = Settings.Global.getInt(getContentResolver(), CustomValue.CAMERA_RECORD_STATUS, 0);
        if (state == 1) {
            mDialogTool.showStopRecordingDialog();
        } else {
            mViewPager.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isNotShowDialog) {
            finish();
        }
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
            compositeDisposable = null;
        }
        loadingDialogDismiss();
        if (CustomValue.IS_3IN) {
            unBingAIDLService();
        }
    }
}
