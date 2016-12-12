package com.pngfi.mediapicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.pngfi.mediapicker.R;
import com.pngfi.mediapicker.engine.ImageLoader;
import com.pngfi.mediapicker.entity.ImageFolder;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by pngfi on 2016/12/5.
 */
public class ImageFolderAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<ImageFolder> imageFolders;
    private int lastSelected = 0;



    public ImageFolderAdapter(Context context, List<ImageFolder> folders) {
        mContext = context;
        if (folders != null && folders.size() > 0) imageFolders = folders;
        else imageFolders = new ArrayList<>();

        mInflater = LayoutInflater.from(context);
    }

    public void refreshData(List<ImageFolder> folders) {
        if (folders != null && folders.size() > 0) imageFolders = folders;
        else imageFolders.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return imageFolders.size();
    }

    @Override
    public ImageFolder getItem(int position) {
        return imageFolders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_photo_dir, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageFolder folder = getItem(position);
        holder.folderName.setText(folder.getName());
        holder.imageCount.setText(folder.getImages().size()+" ");
        ImageLoader.loadImage(mContext, holder.cover,folder.getCover().getPath(),  0, 0);

        if (lastSelected == position) {
            holder.folderCheck.setVisibility(View.VISIBLE);
        } else {
            holder.folderCheck.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void setSelectIndex(int i) {
        if (lastSelected == i) {
            return;
        }
        lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    private class ViewHolder {
        ImageView cover;
        TextView folderName;
        TextView imageCount;
        ImageView folderCheck;

        public ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.iv_photo);
            folderName = (TextView) view.findViewById(R.id.tv_photo_dir);
            imageCount = (TextView) view.findViewById(R.id.tv_photo_num);
            folderCheck = (ImageView) view.findViewById(R.id.iv_select);
            view.setTag(this);
        }
    }
}
