package com.pngfi.mediapicker.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by pngfi on 2016/12/9.
 */

public class MediaHelper {

  public static final int REQUEST_TAKE_PHOTO = 1;

  private String mCurrentPhotoPath;
  private Context mContext;

  public MediaHelper(Context context) {
    this.mContext = context;
  }

  private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
    String imageFileName = "JPEG_" + timeStamp + ".jpg";
    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    if (!storageDir.exists()) {
      if (!storageDir.mkdir()) {
        Log.e("TAG", "Throwing Errors....");
        throw new IOException();
      }
    }

    File image = new File(storageDir, imageFileName);

    // Save a file: path for use with ACTION_VIEW intents
    mCurrentPhotoPath = image.getAbsolutePath();
    return image;
  }


  public Intent dispatchTakePictureIntent() throws IOException {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
      // Create the File where the photo should go
      File file = createImageFile();
      Uri photoFile;

        photoFile = Uri.fromFile(file);


      // Continue only if the File was successfully created
      if (photoFile != null) {
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
      }
    }
    return takePictureIntent;
  }


  public void providerAddMedia() {
    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    if (TextUtils.isEmpty(mCurrentPhotoPath)) {
      return;
    }
    File f = new File(mCurrentPhotoPath);
    Uri contentUri = Uri.fromFile(f);
    mediaScanIntent.setData(contentUri);
    mContext.sendBroadcast(mediaScanIntent);
  }


  public String getCurrentPath(){
    return mCurrentPhotoPath;
  }




}
