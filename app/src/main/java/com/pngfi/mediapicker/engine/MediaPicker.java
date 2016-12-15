package com.pngfi.mediapicker.engine;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;

import com.pngfi.mediapicker.entity.Media;
import com.pngfi.mediapicker.event.ImagePickerFinishEvent;
import com.pngfi.mediapicker.ui.GridActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pngfi on 2016/12/9.
 */

public class MediaPicker {

    private static ImageLoader sImageLoader;


    /**
     * 在Application中初始化
     * @param imageLoader
     */
    public static void initImageLoader(ImageLoader imageLoader){
        sImageLoader=imageLoader;
    }


    public static ImageLoader imageLoader(){
        return sImageLoader;
    }


    public static final String EXTRA_KEY_SELECTED = "extra_key_selected";
    public static final String EXTRA_KEY_SHOW_CAMERA = "extra_key_show_camera";
    public static final String EXTRA_KEY_SELECT_LIMIT = "extra_key_select_limit";
    public static final String EXTRA_KEY_LOAD_TYPE = "load_type_extra";

    public static final String EXTRAK_KEY_CURRENT_POSITION="extra_key_current_position";

    public static final int DEFAULT_SELECT_LIMIT = 9;
    public static final boolean DEFAULT_SHOW_CAMERA = true;


    public static Builder builder() {
        return new Builder();
    }


    public static class Builder{
        private Intent intent;

        //private Transform<T> mTransform;
        private CallBack mCallBack;
        private int type;

        public Builder() {
            intent = new Intent();
            EventBus.getDefault().register(this);
        }

        public void show(Context context, CallBack callBack) {
            if (callBack==null){
                throw  new NullPointerException("callBack must not be null");
            }
            mCallBack=callBack;
            intent.setClass(context, GridActivity.class);
            context.startActivity(intent);
        }

        public Builder imagePicker(){
            type=1;
            intent.putExtra(MediaPicker.EXTRA_KEY_LOAD_TYPE,Scanner.LOAD_TYPE_IMG);
            return this;
        }

        public Builder mediaPicker(){
            type=2;
            intent.putExtra(MediaPicker.EXTRA_KEY_LOAD_TYPE,Scanner.LOAD_TYPE_VIDEO);
            return this;
        }

        public Builder showCamera(boolean showCamera) {
            intent.putExtra(EXTRA_KEY_SHOW_CAMERA, showCamera);
            return this;
        }

        public Builder selectLimit(int selectLimit) {
            intent.putExtra(EXTRA_KEY_SELECT_LIMIT, selectLimit);
            return this;
        }


        public Builder selected(ArrayList<Media> selected) {
            intent.putExtra(EXTRA_KEY_SELECTED, selected);
            return this;
        }

        /*public Builder transform(Transform<T> transform){
            mTransform=transform;
            return this;
        }*/

        @Subscribe(threadMode = ThreadMode.MAIN)
        public  void onImagePickerFinishEvent(ImagePickerFinishEvent event){
            List<Media> list = event.getList();
            mCallBack.onPickerFinished(list);
            EventBus.getDefault().unregister(this);
        }


        public  interface CallBack{
            void onPickerFinished(List<Media> list);
        }




    }





}
