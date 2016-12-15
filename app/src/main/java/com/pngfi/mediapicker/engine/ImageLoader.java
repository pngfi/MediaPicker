package com.pngfi.mediapicker.engine;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pngfi.mediapicker.R;

import java.io.File;

/**
 * Created by pngfi on 2016/11/30.
 * 使用MediaPicker来配置ImageLoader，减少与图片框架之间的耦合
 */

public abstract class ImageLoader {


    /**
     * @param context
     * @param imageView
     * @param path     路径
     * @param width   宽度
     * @param height  高度
     */
    public void loadImage(Context context, ImageView imageView, String path, int width, int height){
        loadImage(context,imageView,path,0,width,height);
    }


     abstract void loadImage(Context context, ImageView imageView, String path,int placeHolderRes,int width, int height);

}
