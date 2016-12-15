package com.pngfi.mediapicker.engine;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.pngfi.mediapicker.entity.ImageFolder;
import com.pngfi.mediapicker.entity.Media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pngfi on 2016/11/30.
 * 扫描手机中的图片，视频
 */

public class Scanner implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String TAG = "Scanner";
    //loadType
    public static final int LOAD_TYPE_IMG = 1;
    public static final int LOAD_TYPE_VIDEO = 2;

    private FragmentActivity context;

    /**
     * 可选值：LOAD_TYPE_IMG
     * LOAD_TYPE_VIDEO
     */
    private int loadType;


    private ArrayList<ImageFolder> imageFolders;


    private OnLoadFishedListener mListenter;


    private final String[] IMG_PROJECTION = {
            MediaStore.Images.Media.DATA,         //路径
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED//添加时间，毫秒值
    };

    private final String[] VIDEO_PROJECTION = {
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION
    };

    public Scanner(FragmentActivity activity, int loadType) {
        this.loadType = loadType;
        context = activity;
        imageFolders = new ArrayList<>();
    }

    public Scanner(FragmentActivity activity) {
        this(activity, LOAD_TYPE_IMG);
    }

    public int getLoadType() {
        return loadType;
    }

    public void setLoadType(int loadType) {
        this.loadType = loadType;
    }

    public void scan(OnLoadFishedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener can't be null");
        }
        mListenter = listener;
        context.getSupportLoaderManager().initLoader(loadType, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        if (id == LOAD_TYPE_IMG) {
            cursorLoader = new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMG_PROJECTION, null, null, IMG_PROJECTION[3] + " DESC");
        } else if (loadType == LOAD_TYPE_VIDEO) {
            cursorLoader = new CursorLoader(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION, null, null, VIDEO_PROJECTION[3] + " DESC");
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.getCount() > 0) {
            ArrayList<Media> allMedias = new ArrayList<>();
            //清除
            imageFolders.clear();
            if (loadType == LOAD_TYPE_IMG) {

                while (data.moveToNext()) {
                    String path = data.getString(data.getColumnIndex(IMG_PROJECTION[0]));
                    long size = data.getLong(data.getColumnIndex(IMG_PROJECTION[1]));
                    String mimeType = data.getString(data.getColumnIndex(IMG_PROJECTION[2]));
                    long addTime = data.getLong(data.getColumnIndex(IMG_PROJECTION[3]));
                    Media image = new Media();
                    image.setPath(path);
                    image.setSize(size);
                    image.setMimeType(mimeType);
                    image.setAddTime(addTime);
                    allMedias.add(image);

                    File imageFile = new File(path);
                    File imageParentFile = imageFile.getParentFile();
                    ImageFolder imageFolder = new ImageFolder();
                    imageFolder.setName(imageParentFile.getName());
                    imageFolder.setPath(imageParentFile.getAbsolutePath());
                    if (!imageFolders.contains(imageFolder)) {
                        ArrayList<Media> images = new ArrayList<>();
                        images.add(image);
                        imageFolder.setCover(image);
                        imageFolder.setImages(images);
                        imageFolder.setName(imageParentFile.getName());
                        imageFolders.add(imageFolder);
                    } else {
                        imageFolders.get(imageFolders.indexOf(imageFolder)).getImages().add(image);
                    }

                }

                //构造所有图片的集合
                ImageFolder allImagesFolder = new ImageFolder();
                allImagesFolder.setName("所有图片");
                allImagesFolder.setPath("/");
                allImagesFolder.setCover(allMedias.get(0));
                allImagesFolder.setImages(allMedias);
                imageFolders.add(0, allImagesFolder);

            } else if (loadType == LOAD_TYPE_VIDEO) {

                while (data.moveToNext()) {
                    String path = data.getString(data.getColumnIndex(VIDEO_PROJECTION[0]));
                    long size = data.getLong(data.getColumnIndex(VIDEO_PROJECTION[1]));
                    String mimeType = data.getString(data.getColumnIndex(VIDEO_PROJECTION[2]));
                    long addTime = data.getLong(data.getColumnIndex(VIDEO_PROJECTION[3]));
                    long duration = data.getLong(data.getColumnIndex(VIDEO_PROJECTION[4]));

                    //视频小于1s 或者大于20M
                    if (duration < 1000 || size / (1024 * 1024) > 20) {
                        continue;
                    }

                    Media video = new Media();
                    video.setPath(path);
                    video.setSize(size);
                    video.setMimeType(mimeType);
                    video.setAddTime(addTime);
                    video.setDuration(duration);
                    allMedias.add(video);
                }

                //把视频的文件夹只有一个
                ImageFolder imageFolder=new ImageFolder();
                imageFolder.setName("视频");
                imageFolder.setImages(allMedias);
                imageFolders.add(imageFolder);
            }

            /**
             * 这里执行close的原因：正常情况下，由于CursorLoader 内部的ContentObserver
             * 会监测数据库的变化，当我们发送一个Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
             * 广播更新数据库时候(mediaHelper.providerAddMedia()),也就会引起数据库的重新查询。
             * 关闭后即不会再查询
             */
            data.close();
            mListenter.onLoadFinshed(loadType, imageFolders);
        }


    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public interface OnLoadFishedListener {
        void onLoadFinshed(int loadType, List<ImageFolder> imageFolders);
    }
}
