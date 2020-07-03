package com.bixin.bxvideolist.model.tools;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class GlideTool {
    public static <T> void load(Context context,
                                T drawable, ImageView imageView, RequestOptions options) {
        Glide.with(context)
                .load(drawable)
                .apply(options)
                .into(imageView);
    }

    public static <T> void load(Context context,
                                T drawable, ImageView imageView, float thumbnail,
                                RequestOptions options) {
        Glide.with(context)
                .load(drawable)
                .apply(options)
                .thumbnail(thumbnail)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(imageView);
    }
}
