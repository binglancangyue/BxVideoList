package com.bixin.bxvideolist.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bixin.bxvideolist.R;
import com.bixin.bxvideolist.adapter.PicturePageAdapter;
import com.bixin.bxvideolist.model.CustomValue;
import com.bixin.bxvideolist.model.receiver.PictureBroadcastReceiver;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ShowPictureActivity extends Activity implements ViewPager.OnPageChangeListener {
    private Context mContext;
    private ArrayList<String> picPaths;
    private ArrayList<String> picNameList;
    private InnerHandler mHandler;

    private ViewPager mViewPager;
    private PicturePageAdapter adapter;
    private PictureBroadcastReceiver receiver;
    private List<ImageView> imageViewList;
    private int pictureIndex = 0;
    private TextView tvPictureName;
    private LinearLayout llStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarVisible(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        this.mContext = this;
        mHandler = new InnerHandler(this);
        initView();
        getData();
        setViewPager();
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
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    private static class InnerHandler extends Handler {
        private final WeakReference<ShowPictureActivity> activityWeakReference;
        private ShowPictureActivity homeActivity;

        private InnerHandler(ShowPictureActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
            homeActivity = activityWeakReference.get();

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (homeActivity != null) {
                homeActivity.setPictureName(msg.arg1);
            }
        }
    }

    private void initView() {
        mViewPager = findViewById(R.id.pager);
        tvPictureName = findViewById(R.id.tv_picture_name);
        ImageView ivBack = findViewById(R.id.iv_back);
        llStatus = findViewById(R.id.ll_status_bar);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("aa", "onClick: finish");
                finish();
            }
        });

    }

    private void getData() {
        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            pictureIndex = intent.getInt("item");
            picPaths = intent.getStringArrayList("picPaths");
            assert picPaths != null;
            setPictureName(pictureIndex);
            getViewList(picPaths);
        }
    }

    private void setPictureName(int position) {
        String name = picPaths.get(position);
        tvPictureName.setText(name.substring(name.lastIndexOf("/") + 1));
    }

    public void dismissControlView() {
        if (llStatus.getVisibility() == View.GONE) {
            llStatus.setVisibility(View.VISIBLE);
        } else {
            llStatus.setVisibility(View.GONE);
        }
    }

    private void setViewPager() {
//        adapter = new PicturePageAdapter(this, views);
        adapter = new PicturePageAdapter(this, imageViewList, picPaths);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(pictureIndex, false);
    }

    private void getViewList(ArrayList<String> picPaths) {
        imageViewList = new ArrayList<>();
        for (String path : picPaths) {
            ImageView imageView = new ImageView(this);
            imageViewList.add(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissControlView();
                }
            });
        }
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("CLOSE_VIDEO_APP");
        receiver = new PictureBroadcastReceiver(this);
        this.registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Message message = Message.obtain();
        message.what = 1;
        message.arg1 = position;
        mHandler.sendMessage(message);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        setResult(10086);
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

}
