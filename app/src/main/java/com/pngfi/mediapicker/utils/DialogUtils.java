package com.pngfi.mediapicker.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by lucky-django on 16/3/16.
 * 显示对话框
 */
public class DialogUtils {

  /**
   * 显示单按钮提示对话框
   */
  public static void showSingleButtonDialog(Context context, String title, String content,
                                            String buttonText, DialogInterface.OnClickListener singleClickListener) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title)
        .setMessage(content)
        .setPositiveButton(buttonText, singleClickListener)
        .create()
        .show();
  }

  /**
   * 显示双按钮提示对话框
   */
  public static void showDoubleButtonDialog(Context context, String title, String content,
                                            String positiveButtonText, String negativeButtonText,
                                            DialogInterface.OnClickListener positiveButtonListener,
                                            DialogInterface.OnClickListener negativeButtonListener) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title)
        .setMessage(content)
        .setPositiveButton(positiveButtonText, positiveButtonListener)
        .setNegativeButton(negativeButtonText, negativeButtonListener)
        .create()
        .show();
  }



}
