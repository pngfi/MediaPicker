package com.pngfi.mediapicker.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.pngfi.mediapicker.engine.ImageLoader;
import com.pngfi.mediapicker.entity.Media;
import com.pngfi.mediapicker.utils.ScreenUtil;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by pngfi on 2016/12/13.
 */
public class ImagePageAdapter extends PagerAdapter {

    private int screenWidth;
    private int screenHeight;
    private ArrayList<Media> images = new ArrayList<>();
    private Context context;
    public OnPhotoTapListener listener;

    public ImagePageAdapter(Context context, ArrayList<Media> images) {
        this.context = context;
        this.images = images;

        DisplayMetrics dm = ScreenUtil.getScreenPix(context);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    public void setData(ArrayList<Media> images) {
        this.images = images;
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        this.listener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(context);
        Media image = images.get(position);
        ImageLoader.loadImage(context, photoView, image.getPath(), screenWidth, screenHeight);
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (listener != null) listener.onPhotoTap(view, x, y);
            }
        });
        container.addView(photoView);
        return photoView;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public interface OnPhotoTapListener {
        void onPhotoTap(View view, float v, float v1);
    }
}
