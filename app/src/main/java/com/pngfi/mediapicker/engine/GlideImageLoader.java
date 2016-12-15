package com.pngfi.mediapicker.engine;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pngfi.mediapicker.R;

import java.io.File;

/**
 * Created by pngfi on 2016/12/1.
 */

public class GlideImageLoader extends ImageLoader {


    @Override
    void loadImage(Context context, ImageView imageView, String path, int placeHolderRes, int width, int height) {
        Glide.with(context)
                .load(Uri.fromFile(new File(path)))
                .centerCrop()
                .placeholder(placeHolderRes)
                .crossFade()
                .into(imageView);
    }


}
