package com.pngfi.mediapicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.pngfi.mediapicker.entity.Media;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Media> mSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPicker.pikcer(MainActivity.this).image().show(new MediaPicker.Builder.CallBack() {
                    @Override
                    public void onPickerFinished(ArrayList<Media> list) {
                        mSelected=list;
                    }
                });
            }
        });

        findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPicker.pikcer(MainActivity.this).video().selectLimit(1).show(new MediaPicker.Builder.CallBack() {
                    @Override
                    public void onPickerFinished(ArrayList<Media> list) {

                    }
                });
            }
        });

        findViewById(R.id.btn_preview_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPicker.preview(MainActivity.this).currentPosition(0).image().selected(mSelected).show(new MediaPicker.Builder.CallBack() {
                    @Override
                    public void onPickerFinished(ArrayList<Media> list) {
                        Log.i("MainActivity",list.size()+"");
                    }
                });
            }
        });
    }
}
