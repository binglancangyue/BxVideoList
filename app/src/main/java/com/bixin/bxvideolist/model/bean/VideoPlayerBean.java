package com.bixin.bxvideolist.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Altair
 * @date :2019.11.19 下午 03:38
 * @description:
 */
public class VideoPlayerBean implements Parcelable {
    private int position;
    private int type;
    private String path;
    private String name;

    public VideoPlayerBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.position);
        dest.writeInt(this.type);
        dest.writeString(this.path);
        dest.writeString(this.name);
    }


    protected VideoPlayerBean(Parcel in) {
        this.position = in.readInt();
        this.type = in.readInt();
        this.path = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<VideoPlayerBean> CREATOR =
            new Parcelable.Creator<VideoPlayerBean>() {
                @Override
                public VideoPlayerBean createFromParcel(Parcel source) {
                    return new VideoPlayerBean(source);
                }

                @Override
                public VideoPlayerBean[] newArray(int size) {
                    return new VideoPlayerBean[size];
                }
            };

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
