package com.pngfi.mediapicker.event;

import com.pngfi.mediapicker.entity.Media;

import java.util.List;

/**
 * Created by pngfi on 2016/12/15.
 */

public class ImagePickerFinishEvent {

    private List<Media> list;

    public List<Media> getList() {
        return list;
    }

    public void setList(List<Media> list) {
        this.list = list;
    }


    public ImagePickerFinishEvent(List<Media> list) {
        this.list = list;
    }
}
