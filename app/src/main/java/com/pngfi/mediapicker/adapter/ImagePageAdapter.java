package com.pngfi.mediapicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.pngfi.mediapicker.R;
import com.pngfi.mediapicker.engine.ImageLoader;
import com.pngfi.mediapicker.engine.MediaPicker;
import com.pngfi.mediapicker.engine.Scanner;
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

    private int type;



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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if (type== Scanner.LOAD_TYPE_IMG){
            PhotoView photoView = new PhotoView(context);
            Media image = images.get(position);
            MediaPicker.imageLoader().loadImage(context, photoView, image.getPath(), screenWidth, screenHeight);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (listener != null) listener.onPhotoTap(view, x, y);
                }
            });
            container.addView(photoView);
            return photoView;
        }else {
            final View view = LayoutInflater.from(context).inflate(R.layout.item_pager_adapter, null);
            final VideoView videoView = (VideoView) view.findViewById(R.id.videoView);
            final PhotoView photoView= (PhotoView) view.findViewById(R.id.iv_frame);
            final ImageView ivPlay= (ImageView) view.findViewById(R.id.iv_play);
            final Media video=images.get(position);
            MediaPicker.imageLoader().loadImage(context, photoView, video.getPath(), screenWidth, screenHeight);
            ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* videoView.setVisibility(View.VISIBLE);
                    videoView.setMediaController(new MediaController(context));
                    videoView.setVideoPath(video.getPath());
                    videoView.start();*/
                    Intent intent=new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    //SDCard卡根目录下/DCIM/Camera/test.mp4文件
                    Uri uri= Uri.parse(video.getPath());
                    intent.setDataAndType(uri, "video/*");
                    context.startActivity(intent);
                }
            });

            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (listener != null) listener.onPhotoTap(view, x, y);
                }
            });
            container.addView(view);
            return view;
        }
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
