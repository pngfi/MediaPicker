package com.pngfi.mediapicker.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pngfi on 2016/12/8.
 *
 */

public class Media implements Parcelable{

    private String path;//完整路径
    private long size;
    private String mimeType;
    private long addTime;//创建时间

    /**
     * 这个字段没有采用继承的原因：
     * java泛型的檫除在复用类的时候会产生很多问题，
     * 采用一种省事的办法
     */
    private long duration;//视频的时长

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Media(){

    }

    protected Media(Parcel in) {
        path = in.readString();
        size = in.readLong();
        mimeType = in.readString();
        addTime = in.readLong();
        duration=in.readLong();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Media media = (Media) o;

        return path != null ? path.equals(media.path) : media.path == null;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeLong(size);
        dest.writeString(mimeType);
        dest.writeLong(addTime);
        dest.writeLong(duration);
    }
}
