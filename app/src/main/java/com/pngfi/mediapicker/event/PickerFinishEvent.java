package com.pngfi.mediapicker.event;

import com.pngfi.mediapicker.entity.Media;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pngfi on 2016/12/15.
 */

public class PickerFinishEvent {

    private ArrayList<Media> list;

    public ArrayList<Media> getList() {
        return list;
    }

    public void setList(ArrayList<Media> list) {
        this.list = list;
    }


    public PickerFinishEvent(ArrayList<Media> list) {
        this.list = list;
    }
}
