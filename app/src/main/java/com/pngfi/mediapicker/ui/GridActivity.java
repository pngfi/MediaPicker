package com.pngfi.mediapicker.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pngfi.mediapicker.R;
import com.pngfi.mediapicker.adapter.GridAdapter;
import com.pngfi.mediapicker.adapter.ImageFolderAdapter;
import com.pngfi.mediapicker.engine.ImagePicker;
import com.pngfi.mediapicker.engine.Scanner;
import com.pngfi.mediapicker.entity.Image;
import com.pngfi.mediapicker.entity.ImageFolder;
import com.pngfi.mediapicker.entity.Media;
import com.pngfi.mediapicker.utils.MediaHelper;
import com.pngfi.mediapicker.utils.PermissionHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by pngfi on 2016/11/30.
 */

public class GridActivity extends BaseActivity implements View.OnClickListener {


    public static final String TAG = "GridActivity";
    private GridView mGridView;
    private ArrayList<Image> photos;

    private GridAdapter mPhotoGridAdapter;
    private ImageFolderAdapter mImageFolderAdapter;

    private FolderPopUpWindow mFolderPopupWindow;
    private TextView mTvBack;
    private TextView mTvTitle;
    private ImageView mImgSelector;
    private View mPopMask;
    private TextView mTvPhotoDir;
    private RelativeLayout mLayoutShowPopupwindow;
    private TextView mTvSureBigPhotos;
    private RelativeLayout mLayoutPhotoDir;


    private List<ImageFolder> mImageFolders;


    private int loadType;

    public static final String LOAD_TYPE_EXTRA = "load_type_extra";

    private ArrayList<Media> mSelected = new ArrayList<>();
    private boolean showCamera;
    private int selectLimit;

    private MediaHelper mediaHelper;
    private String tag;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        initView();
        initData();
        mPermissionHelper.requestPermissions(new PermissionHelper.PermissionListener() {
            @Override
            public void doAfterGrand(String... permission) {

                Scanner scanner = new Scanner(GridActivity.this, Scanner.LOAD_TYPE_IMG);
                scanner.scan(new Scanner.OnLoadFishedListener() {
                    @Override
                    public void onLoadFinshed(List<ImageFolder> imageFolders) {
                        mImageFolders = imageFolders;
                        if (imageFolders.size() == 0) {
                            mPhotoGridAdapter.refreshData(null);
                            Toast.makeText(GridActivity.this, "你的手机没有图片", Toast.LENGTH_LONG).show();
                            return;
                        }
                        mPhotoGridAdapter.refreshData(imageFolders.get(0).getImages());
                        mPhotoGridAdapter.setImageFolderPosition(0);

                        mImageFolderAdapter = new ImageFolderAdapter(GridActivity.this, imageFolders);
                    }
                });
            }

            @Override
            public void doAfterDenied(String... permission) {

            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    private void initData() {
        mPhotoGridAdapter = new GridAdapter(this);
        mGridView.setAdapter(mPhotoGridAdapter);
        //mImageFolderAdapter = new ImageFolderAdapter(this, null);
        mLayoutShowPopupwindow.setOnClickListener(this);
        loadType = getIntent().getIntExtra(LOAD_TYPE_EXTRA, Scanner.LOAD_TYPE_IMG);
        if (loadType == Scanner.LOAD_TYPE_IMG) {
            mTvTitle.setText(R.string.photo);
        } else {
            mTvTitle.setText(R.string.video);
        }
        //参数
        ArrayList<Image> list = getIntent().getParcelableArrayListExtra(ImagePicker.EXTRA_KEY_SELECTED);
        mSelected.addAll(list);
        mPhotoGridAdapter.setSelectedImages(mSelected);

        selectLimit = getIntent().getIntExtra(ImagePicker.EXTRA_KEY_SELECT_LIMIT, ImagePicker.DEFAULT_SELECT_LIMIT);
        showCamera = getIntent().getBooleanExtra(ImagePicker.EXTRA_KEY_SHOW_CAMERA, true);
        mPhotoGridAdapter.setOnSelectedChangeListener(new GridAdapter.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(CheckBox select, View mask, Media media) {
                boolean checked = select.isChecked();
                if (checked) {
                    if (mSelected.size() >= selectLimit) {
                        Toast.makeText(GridActivity.this, "图片不能大于" + selectLimit + "张", Toast.LENGTH_SHORT).show();
                        select.setChecked(false);
                    } else {
                        mask.setVisibility(View.VISIBLE);
                        mSelected.add(media);
                    }
                } else {
                    mSelected.remove(media);
                    mask.setVisibility(View.GONE);
                }

                if (mSelected.size() > 0) {
                    mTvSureBigPhotos.setText(getString(R.string.btn_select_img_number, mSelected.size() + "", selectLimit + ""));
                    mTvSureBigPhotos.setEnabled(true);
                    mTvSureBigPhotos.setSelected(true);
                } else {
                    mTvSureBigPhotos.setSelected(false);
                    mTvSureBigPhotos.setText(getString(R.string.btn_select));
                    mTvSureBigPhotos.setEnabled(false);
                }
            }
        });

        mediaHelper = new MediaHelper(this);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (showCamera && position == 0) {
                    mPermissionHelper.requestPermissions(new PermissionHelper.PermissionListener() {
                        @Override
                        public void doAfterGrand(String... permission) {
                            openCamera();
                        }

                        @Override
                        public void doAfterDenied(String... permission) {

                        }
                    }, Manifest.permission.CAMERA);

                } else {

                }
            }
        });


    }


    private void openCamera() {
        try {
            Intent intent = mediaHelper.dispatchTakePictureIntent();
            startActivityForResult(intent, MediaHelper.REQUEST_TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MediaHelper.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            mediaHelper.providerAddMedia();
            //条件分支未处理
            if (mImageFolders.size() > 0) {
                String path = mediaHelper.getCurrentPath();
                ImageFolder mainFolder = mImageFolders.get(0);
                Image image = new Image();
                image.setPath(path);
                mainFolder.getImages().add(0, image);
                mainFolder.setCover(image);
                mPhotoGridAdapter.notifyDataSetChanged();
            }
        }
    }


    /**
     * 之所以每次点击都创建一个popupwindow为了实现其动画
     * 目前没有找到好的解决方法
     * 因此该Activity要有一个Adapter成员变量，来替FolderPopUpWindow保存一些状态
     */
    private void createFolderPopupWindow() {
        mFolderPopupWindow = new FolderPopUpWindow(this, mImageFolderAdapter);
        mFolderPopupWindow.setOnItemClickListener(new FolderPopUpWindow.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mFolderPopupWindow.dismiss();
                mImageFolderAdapter.setSelectIndex(position);
                ImageFolder imageFolder = (ImageFolder) adapterView.getAdapter().getItem(position);
                if (null != imageFolder) {
                    mPhotoGridAdapter.refreshData(imageFolder.getImages());
                    mPhotoGridAdapter.setImageFolderPosition(position);
                    mTvPhotoDir.setText(imageFolder.getName());
                }
                mGridView.smoothScrollToPosition(0);//滑动到顶部
            }
        });
        mFolderPopupWindow.setMargin(mLayoutShowPopupwindow.getHeight());
    }


    private void initView() {
        mTvBack = (TextView) findViewById(R.id.tv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_show_title);
        mImgSelector = (ImageView) findViewById(R.id.img_selector);
        mGridView = (GridView) findViewById(R.id.gridview);
        mPopMask = findViewById(R.id.pop_mask);
        mTvPhotoDir = (TextView) findViewById(R.id.tv_photo_dir);
        mLayoutShowPopupwindow = (RelativeLayout) findViewById(R.id.layout_show_popupwindow);
        mTvSureBigPhotos = (TextView) findViewById(R.id.tv_sure_big_photos);
        mLayoutPhotoDir = (RelativeLayout) findViewById(R.id.layout_photo_dir);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_show_popupwindow:
                //创建mFolderPopupWindow必需保证有数据，并且能获得layout_show_popupwindow高度
                //目前的代码可能有问题
                createFolderPopupWindow();

                Log.i(TAG,mFolderPopupWindow.isShowing()+"----"+mFolderPopupWindow.toString());
                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.showAtLocation(mLayoutShowPopupwindow, Gravity.NO_GRAVITY, 0, 0);
                    int index = mImageFolderAdapter.getSelectIndex();
                    mFolderPopupWindow.setSelection(index);
                }

                break;
            case R.id.tv_back:
                finish();
                break;

            case R.id.tv_sure_big_photos:
                Intent intent = new Intent();
                if (loadType == Scanner.LOAD_TYPE_IMG) {
                    //intent.putExtra(MediaPicker.EXTRA_RESULT_ITEMS,mediaPicker.getSelectedImages());
                    setResult(Scanner.LOAD_TYPE_IMG, intent);
                } else if (loadType == Scanner.LOAD_TYPE_VIDEO) {
                   /* intent.putExtra(MediaPicker.EXTRA_RESULT_ITEMS,mediaPicker.getSelectedImages());
                    setResult(Scanner.LOAD_TYPE_VIDEO,intent);*/
                }
                finish();
                break;
        }
    }






/*
    if (mImageFolders == null) {
        Log.i("ImageGridActivity", "您的手机没有图片");
        return;
    }
    //点击文件夹按钮
    createPopupFolderList();
    mImageFolderAdapter.refreshData(mImageFolders);  //刷新数据
    if (mFolderPopupWindow.isShowing()) {
        mFolderPopupWindow.dismiss();
    } else {
        mFolderPopupWindow.showAtLocation(mFooterBar, Gravity.NO_GRAVITY, 0, 0);
        //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
        int index = mImageFolderAdapter.getSelectIndex();
        index = index == 0 ? index : index - 1;
        mFolderPopupWindow.setSelection(index);
    }*/


}
