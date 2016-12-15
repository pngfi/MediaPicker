package com.pngfi.mediapicker;

import android.app.Application;

import com.pngfi.mediapicker.engine.GlideImageLoader;
import com.pngfi.mediapicker.engine.MediaPicker;
import com.pngfi.mediapicker.entity.Media;

/**
 * Created by pngfi on 2016/12/14.
 */

public class MediaApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        MediaPicker.initImageLoader(new GlideImageLoader());
    }
}
