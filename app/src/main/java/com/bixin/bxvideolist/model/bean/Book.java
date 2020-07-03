package com.bixin.bxvideolist.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    private int mData;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }

    public static final Parcelable.Creator<Book> CREATOR
            = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    private Book(Parcel in) {
        mData = in.readInt();
    }

    public Book(int mData) {
        this.mData = mData;
    }

    public int getmData() {
        return mData;
    }

    public void setmData(int mData) {
        this.mData = mData;
    }

    public static Creator<Book> getCREATOR() {
        return CREATOR;
    }
}
