package com.pngfi.mediapicker.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.pngfi.mediapicker.utils.PermissionHelper;

/**
 * Created by pngfi on 2016/12/9.
 */

public class BaseActivity extends AppCompatActivity {

    protected PermissionHelper mPermissionHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionHelper=new PermissionHelper(this);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       mPermissionHelper.handleRequestPermissionsResult(requestCode,permissions,grantResults);
    }

}
