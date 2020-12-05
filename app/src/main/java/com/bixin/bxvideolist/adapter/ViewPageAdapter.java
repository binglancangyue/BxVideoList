package com.bixin.bxvideolist.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;



import java.util.List;

public class ViewPageAdapter extends PagerAdapter {
    private List<View> viewList;

    public ViewPageAdapter(List<View> viewList) {
        this.viewList = viewList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView(viewList.get(position));
    }

    /**
     * 将当前View添加到ViewGroup容器中
     * 这个方法，return一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
     */
    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public int getCount() {
        return viewList != null ? viewList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
