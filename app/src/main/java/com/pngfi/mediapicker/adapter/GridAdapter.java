package com.pngfi.mediapicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.pngfi.mediapicker.R;
import com.pngfi.mediapicker.MediaPicker;
import com.pngfi.mediapicker.engine.Scanner;
import com.pngfi.mediapicker.entity.Media;
import com.pngfi.mediapicker.utils.ScreenUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pngfi on 2016/11/30.
 */
public class GridAdapter extends BaseAdapter {

    private static final int ITEM_TYPE_CAMERA = 0;  //相机
    private static final int ITEM_TYPE_NORMAL = 1;  //其他

    private Context context;
    private ArrayList<Media> photos;       //当前需要显示的所有的图片数据

    private int mImageSize;               //每个条目的大小
    private OnSelectedChangeListener listener;   //图片被点击的监听

    private boolean showCamera = true;         //是否显示拍照按钮


    private ArrayList<Media> selectedImages;

    private int imageFolderPosition;//图片文件夹在List中的位置

    private int loadType = Scanner.LOAD_TYPE_IMG;

    public ArrayList<Media> getSelectedImages() {
        return selectedImages;
    }

    public void setSelectedImages(ArrayList<Media> selectedImages) {
        this.selectedImages = selectedImages;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public int getLoadType() {
        return loadType;
    }

    public void setLoadType(int loadType) {
        this.loadType = loadType;
    }

    public boolean showCamera() {
        return showCamera && imageFolderPosition == 0;
    }

    public GridAdapter(Context context, ArrayList<Media> images) {
        this.context = context;
        if (images == null || images.size() == 0)
            this.photos = new ArrayList<>();
        else
            this.photos = images;
        mImageSize = ScreenUtil.getImageItemWidth(context);
    }


    public GridAdapter(Context context) {
        this(context, null);
    }


    public void setImageFolderPosition(int position) {
        imageFolderPosition = position;
    }

    public void refreshData(ArrayList<Media> images) {
        if (images == null || images.size() == 0)
            this.photos = new ArrayList<>();
        else
            this.photos = images;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera())
            return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return showCamera() ? photos.size() + 1 : photos.size();
    }

    @Override
    public Media getItem(int position) {
        if (showCamera()) {
            if (position == 0) return null;
            return photos.get(position - 1);
        } else {
            return photos.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == ITEM_TYPE_CAMERA) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_camera, parent, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            if (loadType == Scanner.LOAD_TYPE_IMG) {
                imageView.setImageResource(R.drawable.ic_take_camera);
            } else {
                imageView.setImageResource(R.drawable.ic_take_video);
            }
            convertView.setLayoutParams(new AbsListView.LayoutParams(mImageSize, mImageSize));
        } else {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_normal, parent, false);
                //让图片是个正方形
                convertView.setLayoutParams(new AbsListView.LayoutParams(mImageSize, mImageSize));
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Media photo = getItem(position);

            holder.cbSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        listener.onSelectedChange(holder.cbSelect, holder.mask, photo);
                    }
                }
            });

            boolean checked = getSelectedImages().contains(photo);
            if (checked) {
                holder.mask.setVisibility(View.VISIBLE);
                holder.cbSelect.setChecked(true);
            } else {
                holder.mask.setVisibility(View.GONE);
                holder.cbSelect.setChecked(false);
            }

            if (loadType == Scanner.LOAD_TYPE_VIDEO) {
                holder.tvTime.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
                holder.tvTime.setText(sdf.format(new Date(photo.getDuration())));

            } else if (loadType == Scanner.LOAD_TYPE_IMG) {
                holder.tvTime.setVisibility(View.GONE);
            }

            MediaPicker.imageLoader().loadImage(context, holder.ivPhoto, photo.getPath(), mImageSize, mImageSize); //显示图片
        }
        return convertView;

    }

    private class ViewHolder {
        public ImageView ivPhoto;
        public CheckBox cbSelect;
        //采用mask来实现透明，而不用setColorFilter是因为在三星4.+版本上会出问题
        public View mask;
        public TextView tvTime;


        public ViewHolder(View view) {
            ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);
            cbSelect = (CheckBox) view.findViewById(R.id.cb_select);
            mask = view.findViewById(R.id.masker);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
        }
    }

    public void setOnSelectedChangeListener(OnSelectedChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSelectedChangeListener {
        void onSelectedChange(CheckBox select, View mask, Media media);
    }
}