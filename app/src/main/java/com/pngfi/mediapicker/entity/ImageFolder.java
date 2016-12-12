package com.pngfi.mediapicker.entity;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by pngfi on 2016/11/30.
 */
public class ImageFolder {

    private String name;
    private String path;
    private Image cover;   //封面图
    private ArrayList<Image> images;

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

    public Image getCover() {
        return cover;
    }

    public void setCover(Image cover) {
        this.cover = cover;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }
}
