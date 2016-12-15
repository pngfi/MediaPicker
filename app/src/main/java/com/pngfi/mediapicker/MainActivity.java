package com.pngfi.mediapicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.pngfi.mediapicker.engine.MediaPicker;
import com.pngfi.mediapicker.entity.Media;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPicker.builder().show(MainActivity.this, new MediaPicker.Builder.CallBack() {
                    @Override
                    public void onPickerFinished(List<Media> list) {
                        for (Media m:list){
                            Log.i("MainActivity",m.getPath());
                        }
                    }
                });
            }
        });
        findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPicker.builder().mediaPicker().show(MainActivity.this, new MediaPicker.Builder.CallBack() {
                    @Override
                    public void onPickerFinished(List<Media> list) {

                    }
                });
            }
        });
    }
}
