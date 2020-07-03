package com.bixin.bxvideolist.model.listener;

import com.bixin.bxvideolist.model.bean.VideoBean;

import java.util.List;

/**
 * @author Altair
 * @date :2019.11.22 下午 04:34
 * @description:
 */
public interface OnRecyclerViewItemListener {
    void onItemClickListener(int position);

    void onItemLongClickListener(int position, List<VideoBean> list);
}
