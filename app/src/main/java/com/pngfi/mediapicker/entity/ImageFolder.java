package com.pngfi.mediapicker.entity;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by pngfi on 2016/11/30.
 */
public class ImageFolder {

    private String name;
    private String path;
    private Media cover;   //封面图
    private ArrayList<Media> images;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageFolder that = (ImageFolder) o;

        return path != null ? path.equals(that.path) : that.path == null;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Media getCover() {
        return cover;
    }

    public void setCover(Media cover) {
        this.cover = cover;
    }

    public ArrayList<Media> getImages() {
        return images;
    }

    public void setImages(ArrayList<Media> images) {
        this.images = images;
    }
}
