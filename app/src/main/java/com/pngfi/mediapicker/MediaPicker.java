package com.pngfi.mediapicker;

import android.content.Context;
import android.content.Intent;

import com.pngfi.mediapicker.engine.ImageLoader;
import com.pngfi.mediapicker.engine.Scanner;
import com.pngfi.mediapicker.entity.Media;
import com.pngfi.mediapicker.event.PickerFinishEvent;
import com.pngfi.mediapicker.ui.GridActivity;
import com.pngfi.mediapicker.ui.PreviewActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by pngfi on 2016/12/9.
 */

public class MediaPicker {

    private static ImageLoader sImageLoader;


    /**
     * 在Application中初始化
     *
     * @param imageLoader
     */
    public static void initImageLoader(ImageLoader imageLoader) {
        sImageLoader = imageLoader;
    }


    public static ImageLoader imageLoader() {
        return sImageLoader;
    }


    public static final String EXTRA_KEY_SELECTED = "extra_key_selected";
    public static final String EXTRA_KEY_SHOW_CAMERA = "extra_key_show_camera";
    public static final String EXTRA_KEY_SELECT_LIMIT = "extra_key_select_limit";
    public static final String EXTRA_KEY_LOAD_TYPE = "load_type_extra";

    public static final String EXTRAK_KEY_CURRENT_POSITION = "extra_key_current_position";

    public static final int DEFAULT_SELECT_LIMIT = 9;


    public static Builder pikcer(Context context) {
        return new Builder(context);
    }

    public static PreviewBuilder preview(Context context){
        return new PreviewBuilder(context);
    }


    public static class Builder {
        protected Intent intent;
        protected Context context;

        private CallBack mCallBack;

        public Builder(Context context) {
            this.context = context;
            intent = new Intent();
            EventBus.getDefault().register(this);
        }

        public void show(CallBack callBack) {
            if (callBack == null) {
                throw new NullPointerException("callBack must not be null");
            }
            mCallBack = callBack;
            context.startActivity(intent);
        }

        public Builder image() {
            intent.putExtra(MediaPicker.EXTRA_KEY_LOAD_TYPE, Scanner.LOAD_TYPE_IMG);
            intent.setClass(context, GridActivity.class);
            return this;
        }

        public Builder video() {
            intent.putExtra(MediaPicker.EXTRA_KEY_LOAD_TYPE, Scanner.LOAD_TYPE_VIDEO);
            intent.setClass(context, GridActivity.class);
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


        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onImagePickerFinishEvent(PickerFinishEvent event) {
            ArrayList<Media> list =  event.getList();
            mCallBack.onPickerFinished(list);
            EventBus.getDefault().unregister(this);
        }


        public interface CallBack {
            void onPickerFinished(ArrayList<Media> list);
        }
    }


    public static class PreviewBuilder extends Builder {

        public PreviewBuilder(Context context) {
            super(context);
        }

        public PreviewBuilder currentPosition(int position) {
            intent.putExtra(EXTRAK_KEY_CURRENT_POSITION,position);
            return this;
        }

        @Override
        public PreviewBuilder video() {
            intent.putExtra(MediaPicker.EXTRA_KEY_LOAD_TYPE, Scanner.LOAD_TYPE_VIDEO);
            intent.setClass(context, PreviewActivity.class);
            return this;
        }

        @Override
        public PreviewBuilder image() {
            intent.putExtra(MediaPicker.EXTRA_KEY_LOAD_TYPE, Scanner.LOAD_TYPE_IMG);
            intent.setClass(context, PreviewActivity.class);
            return this;
        }

    }


}
