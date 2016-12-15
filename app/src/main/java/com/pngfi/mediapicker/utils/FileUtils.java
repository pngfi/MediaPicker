package com.pngfi.mediapicker.utils;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by zhulijun on 1/13/16.
 */
public class FileUtils {

  public static final int MEDIA_TYPE_IMAGE_JPG = 1;
  public static final int MEDIA_TYPE_IMAGE_PNG = 2;
  public static final int MEDIA_TYPE_VIDEO = 3;
  public static final int MEDIA_TYPE_MUSIC = 4;
  public static final int MEDIA_TYPE_COMPRESSED_VIDEO = 5;
  public static final int MEDIA_TYPE_COMPRESSED_AUDIO = 6;
  public static final int MEDIA_TYPE_DECODED_WAV = 7;
  private static final String COMPRESS_DIR = "compressed";
  public static String KZ_DIR = "kuaizhan";


  /**
   * Creates a media file in the {@code Environment.DIRECTORY_PICTURES} directory. The directory
   * is persistent and available to other applications like gallery.
   *
   * @param type Media type. Can be video or image.
   * @return A file object pointing to the newly created file.
   */
  public static File  getOutputMediaFile(Context context, int type) {
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.
    if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
      return getOutputFileInternal(context, type);
    }
    File mediaStorageDir;
    switch (type) {
      default:
      case MEDIA_TYPE_IMAGE_JPG:
      case MEDIA_TYPE_IMAGE_PNG:
        mediaStorageDir =
            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                KZ_DIR);
        break;
      case MEDIA_TYPE_MUSIC:
        mediaStorageDir =
            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                KZ_DIR);
        break;
      case MEDIA_TYPE_VIDEO:
        mediaStorageDir =
            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                KZ_DIR);
        break;
      case MEDIA_TYPE_COMPRESSED_AUDIO:
      case MEDIA_TYPE_DECODED_WAV:
        mediaStorageDir = new File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                .getAbsolutePath() + File.separator + KZ_DIR + File.separator + COMPRESS_DIR);
        break;
      case MEDIA_TYPE_COMPRESSED_VIDEO:
        mediaStorageDir = new File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                .getAbsolutePath() + File.separator + KZ_DIR + File.separator +
                COMPRESS_DIR);
        break;
    }
    // This location works best if you want the created images to be shared
    // between applications and persist after your app has been uninstalled.

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        return getOutputFileInternal(context, type);
      }
    }
    // Create a media file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File mediaFile;
    switch (type) {
      default:
      case MEDIA_TYPE_IMAGE_JPG:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_" + timeStamp + ".jpg");
        break;
      case MEDIA_TYPE_IMAGE_PNG:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_" + timeStamp + ".png");
        break;
      case MEDIA_TYPE_VIDEO:
      case MEDIA_TYPE_COMPRESSED_VIDEO:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_" + timeStamp + ".mp4");
        break;
      case MEDIA_TYPE_MUSIC:
      case MEDIA_TYPE_COMPRESSED_AUDIO:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "AUD_" + timeStamp + ".mp3");
        break;
      case MEDIA_TYPE_DECODED_WAV:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "WAV_" + timeStamp + ".wav");
        break;
    }
    return mediaFile;
  }

  public static File getOutputFileInternal(Context context, int type) {
    String path;
    File internalDir = context.getFilesDir();
    File mediaDir;
    switch (type) {
      default:
      case MEDIA_TYPE_IMAGE_JPG:
      case MEDIA_TYPE_IMAGE_PNG:
        mediaDir = new File(internalDir.getPath()
            + File.separator
            + Environment.DIRECTORY_PICTURES
            + File.separator
            + KZ_DIR);
        break;
      case MEDIA_TYPE_MUSIC:
        mediaDir = new File(internalDir.getPath()
            + File.separator
            + Environment.DIRECTORY_MUSIC
            + File.separator
            + KZ_DIR);
        break;
      case MEDIA_TYPE_COMPRESSED_AUDIO:
      case MEDIA_TYPE_DECODED_WAV:
        mediaDir = new File(internalDir.getPath()
            + File.separator
            + Environment.DIRECTORY_MUSIC
            + File.separator
            + KZ_DIR
            +
            File.separator
            + COMPRESS_DIR
            + File.separator
            + KZ_DIR);
        break;
      case MEDIA_TYPE_COMPRESSED_VIDEO:
        mediaDir = new File(internalDir.getPath()
            + File.separator
            + Environment.DIRECTORY_MOVIES
            + File.separator
            + KZ_DIR
            +
            File.separator
            + COMPRESS_DIR
            + File.separator
            + KZ_DIR);
        break;
    }
    if (!mediaDir.exists()) {
      if (!mediaDir.mkdirs()) {
        path = internalDir.getAbsolutePath();
      } else {
        path = mediaDir.getAbsolutePath();
      }
    } else {
      path = mediaDir.getAbsolutePath();
    }
    // Create a media file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File mediaFile;
    switch (type) {
      default:
      case MEDIA_TYPE_IMAGE_JPG:
        mediaFile = new File(path + File.separator +
            "IMG_" + timeStamp + ".jpg");
        break;
      case MEDIA_TYPE_IMAGE_PNG:
        mediaFile = new File(path + File.separator +
            "IMG_" + timeStamp + ".png");
        break;
      case MEDIA_TYPE_VIDEO:
      case MEDIA_TYPE_COMPRESSED_VIDEO:
        mediaFile = new File(path + File.separator +
            "VID_" + timeStamp + ".mp4");
        break;
      case MEDIA_TYPE_MUSIC:
      case MEDIA_TYPE_COMPRESSED_AUDIO:
        mediaFile = new File(path + File.separator +
            "AUD_" + timeStamp + ".mp3");
        break;
      case MEDIA_TYPE_DECODED_WAV:
        mediaFile = new File(path + File.separator +
            "WAV_" + timeStamp + ".wav");
        break;
    }
    return mediaFile;
  }


}
