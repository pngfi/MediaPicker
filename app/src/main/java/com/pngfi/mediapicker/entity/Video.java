package com.pngfi.mediapicker.entity;

import android.os.Parcel;

/**
 * Created by pngfi on 2016/12/8.
 */

public class Video extends Media{

    private long duration;//毫秒

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Video(){

    }

    protected Video(Parcel in) {
        super(in);
        duration=in.readLong();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };



}
