package com.pngfi.mediapicker.ui;

import android.Manifest;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
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
import com.pngfi.mediapicker.engine.MediaPicker;
import com.pngfi.mediapicker.engine.Scanner;
import com.pngfi.mediapicker.entity.ImageFolder;
import com.pngfi.mediapicker.entity.Media;
import com.pngfi.mediapicker.event.ImagePickerFinishEvent;
import com.pngfi.mediapicker.utils.MediaHelper;
import com.pngfi.mediapicker.utils.PermissionHelper;
import com.pngfi.mediapicker.view.RecordVideoActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by pngfi on 2016/11/30.
 */

public class GridActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_KEY_CURRENT_FOLDER_LIST = "extra_key_current_folder_list";

    public static final int REQUEST_CODE_IMAGE_PREVIEW = 100;

    public static final String TAG = "GridActivity";
    private GridView mGridView;

    private GridAdapter mPhotoGridAdapter;
    private ImageFolderAdapter mImageFolderAdapter;

    private FolderPopUpWindow mFolderPopupWindow;
    private TextView mTvBack;
    private TextView mTvTitle;

    private TextView mTvPhotoDir;
    private RelativeLayout mShowPopupwindow;
    private TextView mTvFinish;
    private RelativeLayout mLayoutPhotoDir;


    private List<ImageFolder> mImageFolders;


    private int loadType;


    private ArrayList<Media> mSelected = new ArrayList<>();
    private boolean showCamera;
    private int selectLimit;

    private MediaHelper mediaHelper;
    private ImageView ivIndicator;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        initView();
        initData();
        initEvent();
    }


    private void initEvent() {
        mTvFinish.setOnClickListener(this);
        mTvBack.setOnClickListener(this);
    }

    private void initData() {
        mPhotoGridAdapter = new GridAdapter(this);

        mGridView.setAdapter(mPhotoGridAdapter);
        //加载类型，图片或者视频
        loadType = getIntent().getIntExtra(MediaPicker.EXTRA_KEY_LOAD_TYPE, Scanner.LOAD_TYPE_IMG);
        mPhotoGridAdapter.setLoadType(loadType);

        if (loadType == Scanner.LOAD_TYPE_IMG) {
            mTvTitle.setText(R.string.image);
            //图片的话要显示popwindow
            mShowPopupwindow.setOnClickListener(this);
        } else if (loadType == Scanner.LOAD_TYPE_VIDEO) {
            mTvTitle.setText(R.string.video);
            //目录
            mTvPhotoDir.setText(R.string.video);
            //隐藏indicator
            ivIndicator.setVisibility(View.GONE);
        }
        //选中的
        ArrayList<Media> list = getIntent().getParcelableArrayListExtra(MediaPicker.EXTRA_KEY_SELECTED);
        if (list != null) {
            mSelected.addAll(list);
        }
        mPhotoGridAdapter.setSelectedImages(mSelected);

        //选择最大数量限制
        selectLimit = getIntent().getIntExtra(MediaPicker.EXTRA_KEY_SELECT_LIMIT, MediaPicker.DEFAULT_SELECT_LIMIT);
        //是否显示相机
        showCamera = getIntent().getBooleanExtra(MediaPicker.EXTRA_KEY_SHOW_CAMERA, true);
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
                    mask.setVisibility(View.GONE);
                    mSelected.remove(media);
                }
                changeTvFinish();
            }
        });

        mediaHelper = new MediaHelper(this);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPhotoGridAdapter.showCamera() && position == 0) {
                    mPermissionHelper.requestPermissions(new PermissionHelper.PermissionListener() {
                        @Override
                        public void doAfterGrand(String... permission) {
                            if (loadType == Scanner.LOAD_TYPE_IMG) {
                                mediaHelper.takePicture();
                            } else {
                                mediaHelper.takeVideo();
                            }
                        }

                        @Override
                        public void doAfterDenied(String... permission) {
                        }
                    }, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
                } else {
                    Intent intent = new Intent(GridActivity.this, ImagePreviewActivity.class);
                    intent.putExtra(EXTRA_KEY_CURRENT_FOLDER_LIST, mImageFolders.get(mImageFolderAdapter.getSelectIndex()).getImages());
                    intent.putExtra(MediaPicker.EXTRA_KEY_SELECTED, mSelected);
                    int curPositon = mPhotoGridAdapter.showCamera() ? position - 1 : position;
                    intent.putExtra(MediaPicker.EXTRAK_KEY_CURRENT_POSITION, curPositon);
                    startActivityForResult(intent, REQUEST_CODE_IMAGE_PREVIEW);
                }
            }
        });


        mPermissionHelper.requestPermissions(new PermissionHelper.PermissionListener() {
            @Override
            public void doAfterGrand(String... permission) {

                Scanner scanner = new Scanner(GridActivity.this, loadType);
                scanner.scan(new Scanner.OnLoadFishedListener() {
                    @Override
                    public void onLoadFinshed(int loadType, List<ImageFolder> imageFolders) {
                        mImageFolders = imageFolders;
                        if (loadType == Scanner.LOAD_TYPE_IMG) {
                            mImageFolderAdapter = new ImageFolderAdapter(GridActivity.this, mImageFolders);
                        } else if (loadType == Scanner.LOAD_TYPE_VIDEO) {

                        }
                        if (imageFolders.size() == 0) {
                            mPhotoGridAdapter.refreshData(null);
                            Toast.makeText(GridActivity.this, "你的手机没有图片", Toast.LENGTH_LONG).show();
                            return;
                        }
                        mPhotoGridAdapter.refreshData(imageFolders.get(0).getImages());
                        mPhotoGridAdapter.setImageFolderPosition(0);
                    }
                });
            }

            @Override
            public void doAfterDenied(String... permission) {

            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    private void changeTvFinish() {
        if (mSelected.size() > 0) {
            mTvFinish.setText(getString(R.string.btn_select_img_number, mSelected.size() + "", selectLimit + ""));
            mTvFinish.setEnabled(true);
            mTvFinish.setSelected(true);
        } else {
            mTvFinish.setSelected(false);
            mTvFinish.setText(getString(R.string.btn_select));
            mTvFinish.setEnabled(false);
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
                Media image = new Media();
                image.setPath(path);
                mainFolder.getImages().add(0, image);
                mainFolder.setCover(image);
                mPhotoGridAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == MediaHelper.REQUEST_TAKE_VIDEO && resultCode == RESULT_OK) {
            String videoPath=data.getStringExtra(RecordVideoActivity.EXTRA_VIDEO_PATH);
            MediaMetadataRetriever retriever=new MediaMetadataRetriever();
            retriever.setDataSource(videoPath);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            ImageFolder mainFolder = mImageFolders.get(0);
            Media video = new Media();
            video.setPath(videoPath);
            video.setDuration(Long.parseLong(duration));
            mainFolder.getImages().add(0, video);
            mPhotoGridAdapter.notifyDataSetChanged();

        } else if (requestCode == REQUEST_CODE_IMAGE_PREVIEW && resultCode == RESULT_CANCELED) {
            ArrayList<Media> imageList = data.getParcelableArrayListExtra(MediaPicker.EXTRA_KEY_SELECTED);
            mSelected.clear();
            mSelected.addAll(imageList);
            mPhotoGridAdapter.notifyDataSetChanged();
            changeTvFinish();
        } else if (requestCode == REQUEST_CODE_IMAGE_PREVIEW && resultCode == RESULT_OK) {
            ArrayList<Media> imageList = data.getParcelableArrayListExtra(MediaPicker.EXTRA_KEY_SELECTED);
            EventBus.getDefault().post(new ImagePickerFinishEvent(imageList));
            finish();
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
        mFolderPopupWindow.setMargin(mLayoutPhotoDir.getHeight());
    }


    private void initView() {
        mTvBack = (TextView) findViewById(R.id.tv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_show_title);
        mGridView = (GridView) findViewById(R.id.gridview);
        mTvPhotoDir = (TextView) findViewById(R.id.tv_photo_dir);
        mShowPopupwindow = (RelativeLayout) findViewById(R.id.layout_show_popupwindow);
        mTvFinish = (TextView) findViewById(R.id.tv_finish);
        mLayoutPhotoDir = (RelativeLayout) findViewById(R.id.layout_photo_dir);

        ivIndicator = (ImageView) findViewById(R.id.iv_indicator);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_show_popupwindow:
                //创建mFolderPopupWindow必需保证有数据，并且能获得layout_show_popupwindow高度
                //目前的代码可能有问题
                createFolderPopupWindow();

                Log.i(TAG, mFolderPopupWindow.isShowing() + "----" + mFolderPopupWindow.toString());
                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.showAtLocation(mShowPopupwindow, Gravity.NO_GRAVITY, 0, 0);
                    int index = mImageFolderAdapter.getSelectIndex();
                    mFolderPopupWindow.setSelection(index);
                }

                break;
            case R.id.tv_back:
                finish();
                break;

            case R.id.tv_finish:
                Intent intent = new Intent();
                if (loadType == Scanner.LOAD_TYPE_IMG) {
                    EventBus.getDefault().post(new ImagePickerFinishEvent(mSelected));
                    finish();
                    setResult(Scanner.LOAD_TYPE_IMG, intent);
                } else if (loadType == Scanner.LOAD_TYPE_VIDEO) {
                   /* intent.putExtra(MediaPicker.EXTRA_RESULT_ITEMS,mediaPicker.getSelectedImages());
                    setResult(Scanner.LOAD_TYPE_VIDEO,intent);*/
                }
                finish();
                break;
        }
    }


}
