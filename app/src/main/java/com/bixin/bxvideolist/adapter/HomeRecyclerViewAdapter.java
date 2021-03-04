package com.bixin.bxvideolist.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bixin.bxvideolist.R;
import com.bixin.bxvideolist.model.CustomValue;
import com.bixin.bxvideolist.model.bean.VideoBean;
import com.bixin.bxvideolist.model.listener.OnRecyclerViewItemListener;
import com.bixin.bxvideolist.model.tools.GlideTool;
import com.bumptech.glide.request.RequestOptions;

import java.lang.ref.WeakReference;
import java.util.List;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder> {


/*    private RequestOptions options;
    private LayoutInflater mInflater;
    private Context mContext;
    private List<VideoBean> mData;
    private boolean mIsShowSelect;
    private OnRecyclerViewItemListener onItemListener;
    private int mItemCount = 12;

    public void setOnRecyclerViewItemListener(OnRecyclerViewItemListener listener) {
        this.onItemListener = listener;
    }

    public HomeRecyclerViewAdapter(Context context, List<VideoBean> mData) {
        this.mContext = context;
        this.mData = mData;
        this.mInflater = LayoutInflater.from(mContext);
        options = new RequestOptions()
                .placeholder(R.drawable.no_thumbnail);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_recyclerview_file_info, parent,
                false));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!mIsShowSelect) {
            holder.ivSelect.setVisibility(View.INVISIBLE);
        } else {
            holder.ivSelect.setVisibility(View.VISIBLE);
        }
        if (position < mData.size()) {
            holder.tvName.setText(mData.get(position).getName());
            holder.tvSize.setText(mData.get(position).getSize());
            GlideTool.load(mContext, mData.get(position).getPath(), holder.ivImg, 0.2f, options);
            if (mData.get(position).getSelect()) {
                Glide.with(mContext).load(R.drawable.selected).into(holder.ivSelect);
            } else {
                Glide.with(mContext).load(R.drawable.not_selected).into(holder.ivSelect);

            }
        }
    }

    @Override
    public int getItemCount() {
        if (mData.size() > mItemCount) {
            return mItemCount;
        }
        return mData.size();
    }

    public void setIsShowSelect(boolean is) {
        mIsShowSelect = is;
    }

    public boolean getIsShowSelect() {
        return mIsShowSelect;
    }

    public void setData(List<VideoBean> data) {
        this.mData = data;
    }

    public void setItemCount(int count) {
        mItemCount = count;
    }

    final class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImg;
        TextView tvName;
        TextView tvSize;
        ImageView ivSelect;
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemListener != null) {
                    //xRecyclerView 自带头部和尾部,position=position-1;
                    onItemListener.onItemClickListener(getAdapterPosition() - 1);
                }
            }
        };
        private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemListener != null) {
                    onItemListener.onItemLongClickListener(getAdapterPosition() - 1, null);
                }
                return true;
            }
        };

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.Thumbnail);
            tvName = itemView.findViewById(R.id.name);
            tvSize = itemView.findViewById(R.id.size);
            ivSelect = itemView.findViewById(R.id.select);
            ivImg.setOnClickListener(mOnClickListener);
            ivImg.setOnLongClickListener(mOnLongClickListener);
        }
    }*/


    private RequestOptions options;
    private LayoutInflater mInflater;
    private Context mContext;
    private List<VideoBean> mData;
    private boolean mIsShowSelect;
    private OnRecyclerViewItemListener onItemListener;
    private int mItemCount = 12;

    public void setOnRecyclerViewItemListener(OnRecyclerViewItemListener listener) {
        this.onItemListener = listener;
    }


    public HomeRecyclerViewAdapter(Context context, List<VideoBean> list) {
        this.mContext = context;
        this.mData = list;
        this.mInflater = LayoutInflater.from(mContext);
        options = new RequestOptions()
                .placeholder(R.drawable.no_thumbnail);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;
        if (CustomValue.IS_KD003) {
            layoutId = R.layout.item_recyclerview_file_info_kd003;
        } else {
            layoutId = R.layout.item_recyclerview_file_info;
        }
        return new ViewHolder(mInflater.inflate(layoutId, parent,
                false), this);
    }

//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mIsShowSelect) {
            holder.ivSelect.setVisibility(View.VISIBLE);
            holder.ivSelect.setSelected(mData.get(position).getSelect());
        } else {
            holder.ivSelect.setVisibility(View.INVISIBLE);
        }
        if (position < mData.size()) {
            holder.tvName.setText(mData.get(position).getName());
            holder.tvSize.setText(mData.get(position).getSize());

            GlideTool.load(mContext, mData.get(position).getPath(), holder.ivImg, 0.2f, options);
//            if (mData.get(position).getSelect()) {
//                Glide.with(mContext).load(R.drawable.selected).into(holder.ivSelect);
//            } else {
//                Glide.with(mContext).load(R.drawable.not_selected).into(holder.ivSelect);
//            }
        }
    }

    @Override
    public int getItemCount() {
        if (mData.size() > mItemCount) {
            return mItemCount;
        }
        return mData.size();
    }

    public void setIsShowSelect(boolean is) {
        this.mIsShowSelect = is;
    }

    public boolean getIsShowSelect() {
        return mIsShowSelect;
    }

    public void setData(List<VideoBean> data) {
        this.mData = data;
    }

    public void setItemCount(int count) {
        mItemCount = count;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImg;
        TextView tvName;
        TextView tvSize;
        ImageView ivSelect;
        HomeRecyclerViewAdapter mAdapter;

        ViewHolder(@NonNull View itemView, HomeRecyclerViewAdapter adapter) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.Thumbnail);
            tvName = itemView.findViewById(R.id.name);
            tvSize = itemView.findViewById(R.id.size);
            ivSelect = itemView.findViewById(R.id.select);
            itemView.setOnClickListener(mOnClickListener);
            itemView.setOnLongClickListener(mOnLongClickListener);
            WeakReference<HomeRecyclerViewAdapter> weakReference = new WeakReference<>(adapter);
            mAdapter = weakReference.get();
        }

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.onItemListener != null) {
                    //xRecyclerView 自带头部和尾部,position=position-1;
                    mAdapter.onItemListener.onItemClickListener(getAdapterPosition() - 1);
                }
            }
        };
        private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mAdapter.onItemListener != null) {
                    ivSelect.setSelected(true);
                    mAdapter.onItemListener.onItemLongClickListener(getAdapterPosition() - 1,
                            mAdapter.mData);
                }
                return true;
            }
        };
    }

}
