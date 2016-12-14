package com.pngfi.mediapicker.utils;

import android.content.Context;
import android.util.DisplayMetrics;


public class ScreenUtil {

    /** 获得状态栏的高度 */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }


    /** 三列取Item宽度 */
    public static int getImageItemWidth(Context activity) {
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        int cols = 3;
        int columnSpace = (int) (2 * activity.getResources().getDisplayMetrics().density);
        return (screenWidth - columnSpace * (cols - 1)) / cols;
    }

    /**
     * 获取手机大小（分辨率）
     */
    public static DisplayMetrics getScreenPix(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        return metrics;
    }
}
