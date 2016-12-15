package com.pngfi.mediapicker.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pngfi.mediapicker.R;
import com.pngfi.mediapicker.adapter.ImagePageAdapter;
import com.pngfi.mediapicker.engine.MediaPicker;
import com.pngfi.mediapicker.entity.Media;
import com.pngfi.mediapicker.utils.SystemBarTintManager;
import com.pngfi.mediapicker.utils.ScreenUtil;

import java.util.ArrayList;


/**
 * Created by pngfi on 2016/12/13.
 */

public class ImagePreviewActivity extends BaseActivity {

    private ViewPager mViewPager;
    private CheckBox cbSelect;
    private TextView tvBack;

    private View topBar;
    private View bottomBar;


    private ArrayList<Media> mSelected;//当前选中的图片
    private ArrayList<Media> mImageList;
    private SystemBarTintManager tintManager;
    private View root;
    private TextView tvFinish;


    private int mCurrentPosition;//当前第几张图片

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photos);
        initView();
        initData();
    }


    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        cbSelect = (CheckBox) findViewById(R.id.cb_select);
        tvBack = (TextView) findViewById(R.id.tv_back);
        cbSelect.setVisibility(View.VISIBLE);
        topBar = findViewById(R.id.layout_top_bar);
        bottomBar = findViewById(R.id.bottom_bar);
        root = findViewById(R.id.root);
        tvFinish= (TextView) findViewById(R.id.tv_finish);
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);  //设置上方状态栏的颜色*/

        //设置了windowTranslucentStatus后，添加一个margin正常显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) topBar.getLayoutParams();
            params.topMargin = ScreenUtil.getStatusHeight(this);
            topBar.setLayoutParams(params);
        }
    }


    private void initData() {
        mSelected = getIntent().getParcelableArrayListExtra(MediaPicker.EXTRA_KEY_SELECTED);
        mImageList = getIntent().getParcelableArrayListExtra(GridActivity.EXTRA_KEY_CURRENT_FOLDER_LIST);
        mCurrentPosition=getIntent().getIntExtra(MediaPicker.EXTRAK_KEY_CURRENT_POSITION,0);


        ImagePageAdapter imagePageAdapter = new ImagePageAdapter(this, mImageList);
        mViewPager.setAdapter(imagePageAdapter);

        //当前的图片无法触发onPageSelected
        if (mSelected.contains(mImageList.get(mCurrentPosition))){
            cbSelect.setChecked(true);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mSelected.contains(mImageList.get(position))) {
                    cbSelect.setChecked(true);
                } else {
                    cbSelect.setChecked(false);
                }
                mCurrentPosition=position;
            }
        });

        imagePageAdapter.setOnPhotoTapListener(new ImagePageAdapter.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                if (topBar.getVisibility() == View.VISIBLE) {
                    topBar.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.top_out));
                    bottomBar.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    topBar.setVisibility(View.GONE);
                    bottomBar.setVisibility(View.GONE);
                    tintManager.setStatusBarTintResource(R.color.transparent);
                    if (Build.VERSION.SDK_INT >= 16)
                        root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                } else {
                    topBar.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.top_in));
                    bottomBar.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                    topBar.setVisibility(View.VISIBLE);
                    bottomBar.setVisibility(View.VISIBLE);
                    tintManager.setStatusBarTintResource(R.color.colorPrimary);
                    if (Build.VERSION.SDK_INT >= 16)
                        root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        changeTvFinish();

        cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = cbSelect.isChecked();
                Media image = mImageList.get(mCurrentPosition);
                if (checked){
                    //这里要修改，记得改
                    if (mSelected.size()>=9){
                        cbSelect.setChecked(false);
                        Toast.makeText(mContext,getString(R.string.image_exceed_limit_prompt,9+""),Toast.LENGTH_LONG).show();
                    }
                    else {
                        mSelected.add(image);
                    }
                }else {
                    mSelected.remove(image);
                }

                changeTvFinish();
            }
        });

        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mViewPager.setCurrentItem(mCurrentPosition,false);
    }


    private void changeTvFinish(){
        //这里硬编码了
        if (mSelected.size() > 0) {
            tvFinish.setText(getString(R.string.btn_select_img_number, mSelected.size() + "", 9 + ""));
            tvFinish.setEnabled(true);
            tvFinish.setSelected(true);
        } else {
            tvFinish.setSelected(false);
            tvFinish.setText(getString(R.string.btn_select));
            tvFinish.setEnabled(false);
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        back();
    }


    private void back(){
        Intent data=new Intent();
        data.putExtra(MediaPicker.EXTRA_KEY_SELECTED,mSelected);
        setResult(RESULT_CANCELED,data);
        finish();
    }

}



