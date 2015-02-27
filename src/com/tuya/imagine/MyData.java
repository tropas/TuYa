/*
 * 版    权： 大肥与胖毛工作室
 * 创建人: d.cc
 * 创建时间: 2013-11-18
 */

package com.tuya.imagine;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.tuya.baselib.util.BitmapUtil;
import com.tuya.baselib.util.SharedPreferencesUtil;
import com.tuya.imagine.app.BaseApplication;
import com.tuya.imagine.setting.Appsetting;

/**
 * 
 * @author 胖毛
 * @date 2013-11-18
 */
public class MyData {

    /**
     * 打开使用的图片路径
     */
    public static String PicPath = null;

    /**
     * 整个应用使用的Bitmap
     */
    public static Bitmap ShowBitmap;

    /**
     * 画质0:低1:一般2:高清
     */
    public static int PicQuality = 2;

    public static final int MAX_BITMAP_LENGHT = 1200;

    public static final String EXTRA_PIC_PATH = "galleryPath";

    /**
     * 获取载入的图片，当Bitmap被销毁，通过路径重新生成
     * 
     * @return
     */
    public static Bitmap getShowBitmap() {
        if (ShowBitmap == null) {
            if (PicPath != null) {
                ShowBitmap = BitmapUtil.getBitmap(PicPath, Config.ARGB_8888, MAX_BITMAP_LENGHT);
            } else {
                SharedPreferences sharePath = SharedPreferencesUtil.getSharedPreferences(BaseApplication.getApplication());
                String path = sharePath.getString(MyConstant.SHARE_PIC_PATH, PicPath);
                ShowBitmap = BitmapUtil.getBitmap(path, Config.ARGB_8888, MAX_BITMAP_LENGHT);
            }
        }
        return ShowBitmap;

    }

    public static String getRootPath() {
        SharedPreferences sharePath = SharedPreferencesUtil.getSharedPreferences(BaseApplication.getApplication());
        return sharePath.getString(MyConstant.KEY_ROOT_PATH, Appsetting.APP_PATH);
    }

    public static void setRootPath(String path) {
        SharedPreferences sharePath = SharedPreferencesUtil.getSharedPreferences(BaseApplication.getApplication());
        sharePath.edit().putString(MyConstant.KEY_ROOT_PATH, path).commit();
    }

    public static int getLocalQuality() {
        SharedPreferences sharePreferences = SharedPreferencesUtil.getSharedPreferences(BaseApplication.getApplication());
        PicQuality = sharePreferences.getInt(MyConstant.KEY_PIC_QUALITY, 2);
        return PicQuality;
    }

    public static void setPicQuality(int quality) {
        SharedPreferences sharePath = SharedPreferencesUtil.getSharedPreferences(BaseApplication.getApplication());
        sharePath.edit().putInt(MyConstant.KEY_PIC_QUALITY, quality).commit();
        PicQuality = quality;
    }
}
