package com.bixin.bxvideolist.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PicturePageAdapter extends PagerAdapter {
    private Context mContext;
    private List<ImageView> mImageViewList;
    private ArrayList<String> mPicPathList;

    public PicturePageAdapter(Context mContext, List<ImageView> imageViews) {
        this.mContext = mContext;
        this.mImageViewList = imageViews;
    }

    public PicturePageAdapter(Context mContext, List<ImageView> imageViews,
                              ArrayList<String> picPaths) {
        this.mContext = mContext;
        this.mImageViewList = imageViews;
        this.mPicPathList = picPaths;
    }

    @Override
    public int getCount() {
        return mImageViewList != null ? mImageViewList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mImageViewList.get(position));
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(mImageViewList.get(position));
        Glide.with(mContext).load(mPicPathList.get(position)).into((ImageView) mImageViewList.get(position));
        return mImageViewList.get(position);
    }
}
