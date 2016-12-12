package com.pngfi.mediapicker;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.pngfi.mediapicker.engine.ImagePicker;
import com.pngfi.mediapicker.entity.Image;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.builder().selected(new ArrayList<Image>()).open(MainActivity.this);
            }
        });
    }
}
