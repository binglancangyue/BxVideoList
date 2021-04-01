package com.bixin.bxvideolist.view.customview;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bixin.bxvideolist.R;
import com.bixin.bxvideolist.adapter.HomeRecyclerViewAdapter;
import com.bixin.bxvideolist.model.CustomValue;
import com.bixin.bxvideolist.view.activity.HomeActivity;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Created by Administrator on 2016/10/31 0031.
 */
public class CustomRecyclerView extends ViewGroup {
    public LinearLayout constraintLayout;
    public XRecyclerView xRecyclerView;
    public Button selectAll;
    public Button lock;
    public Button delete;
    public Button cancel;
    private int itemCount = 12;
    private HomeRecyclerViewAdapter mRecyclerViewAdapter;
    private Context mContext;
    private HomeActivity mActivity;

    public CustomRecyclerView(Context context, AttributeSet attrs, int type) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater mInflater = LayoutInflater.from(context);
        constraintLayout = (LinearLayout) mInflater.inflate(R.layout.button_list, null);
        constraintLayout.setVisibility(View.GONE);
        selectAll = constraintLayout.findViewById(R.id.select_all);
        lock = constraintLayout.findViewById(R.id.lock);
        delete = constraintLayout.findViewById(R.id.delete);
        cancel = constraintLayout.findViewById(R.id.cancel);
        if (type == 1) {
            lock.setText(R.string.unlock);
        }
        if (type == 2) {
            lock.setVisibility(View.GONE);
        }
        xRecyclerView = new XRecyclerView(context);
        xRecyclerView.setPullRefreshEnabled(false);
        xRecyclerView.setLoadingMoreEnabled(true);

        xRecyclerView.setHasFixedSize(true);
        xRecyclerView.setItemViewCacheSize(20);
        xRecyclerView.setDrawingCacheEnabled(true);
        xRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        setLoadingListener();
        setRecyclerViewScrollListener();

//        xRecyclerView.setLoadingMoreProgressStyle(11);
        ((DefaultItemAnimator) xRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (CustomValue.IS_KD003) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            xRecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(XRecyclerView.VERTICAL);
            xRecyclerView.setLayoutManager(layoutManager);
//            xRecyclerView.addItemDecoration(new CustomDecoration(context, RecyclerView.VERTICAL,
//                    R.drawable.transparent_dividing_verticall_line, 0));

            DividerItemDecoration divider =
                    new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
            divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(context,
                    R.drawable.transparent_dividing_verticall_line)));
            xRecyclerView.addItemDecoration(divider);
        }
        this.addView(constraintLayout);
        this.addView(xRecyclerView);
    }

    private HomeActivity setHomeActivity(HomeActivity homeActivity) {
        WeakReference<HomeActivity> weakReference = new WeakReference<>(homeActivity);
        this.mActivity = weakReference.get();
        return mActivity;
    }

    public void setLoadingListener() {
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                itemCount += 10;
                mRecyclerViewAdapter.setItemCount(itemCount);
                xRecyclerView.loadMoreComplete();
                Log.d("aaa", "onLoadMore: " + itemCount);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int cCount = getChildCount();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        int ww;
        int hh;

        if (constraintLayout.getVisibility() != View.GONE) {
            ww = MeasureSpec.makeMeasureSpec(sizeWidth, MeasureSpec.EXACTLY);
            hh = MeasureSpec.makeMeasureSpec(sizeHeight, MeasureSpec.AT_MOST);
            constraintLayout.measure(ww, hh);
        } else {
            ww = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
            hh = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
            constraintLayout.measure(ww, hh);
        }
        ww = MeasureSpec.makeMeasureSpec(sizeWidth, MeasureSpec.EXACTLY);
        hh = MeasureSpec.makeMeasureSpec(sizeHeight - constraintLayout.getMeasuredHeight(),
                MeasureSpec.EXACTLY);
        xRecyclerView.measure(ww, hh);
        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        xRecyclerView.layout(0, 0, xRecyclerView.getMeasuredWidth(),
                xRecyclerView.getMeasuredHeight());
        constraintLayout.layout(0, xRecyclerView.getMeasuredHeight(),
                constraintLayout.getMeasuredWidth(),
                xRecyclerView.getMeasuredHeight() + constraintLayout.getMeasuredWidth());
    }

    public void setAdapter(HomeRecyclerViewAdapter adapter) {
        this.mRecyclerViewAdapter = adapter;
        xRecyclerView.setAdapter(adapter);
    }

//    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
//        xRecyclerView.setOnItemClickListener(listener);
//    }
//
//    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
//        xRecyclerView.setOnItemLongClickListener(listener);
//    }

    public void setLongClickable(boolean bool) {
        xRecyclerView.setLongClickable(bool);
    }

//    public int getFirstVisiblePosition() {
//        return mylistview.getFirstVisiblePosition();
//    }
//
//    public int getLastVisiblePosition() {
//        return mylistview.getLastVisiblePosition();
//    }

//    public int getCount() {
//        return mylistview.getCount();
//    }

    public void setButtonOnClickListener(OnClickListener OnClickListener) {
        selectAll.setOnClickListener(OnClickListener);
        lock.setOnClickListener(OnClickListener);
        delete.setOnClickListener(OnClickListener);
        cancel.setOnClickListener(OnClickListener);
    }

    public void setHasButton(boolean has) {
        if (has) {
            constraintLayout.setVisibility(View.VISIBLE);
        } else {
            constraintLayout.setVisibility(View.GONE);
        }
    }

    private void setRecyclerViewScrollListener() {
        xRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                glideLoadPictureControl(newState);
            }
        });
    }

    /**
     * 根据recyclerView scroll状态判断是否加载图片
     *
     * @param newState 状态值
     */
    private void glideLoadPictureControl(int newState) {
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            Glide.with(mContext).resumeRequests();//恢复Glide加载图片
        } else {
            Glide.with(mContext).pauseRequests();//禁止Glide加载图片
        }
    }
}
