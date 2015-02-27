/*
 * 版    权： 大肥与胖毛工作室
 * 创建人: d.cc
 * 创建时间: 2013-11-16
 */

package com.tuya.imagine.setting;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;

import com.tuya.baselib.util.SdcardUtil;

/**
 * 
 * @author 胖毛
 * @date 2013-11-16
 */
public class Appsetting {

    public static String SD_PATH;
    public static String APP_PATH;
    /**
     * 临时图片存放路径
     */
    public static String TEMP_PATH;
    public static String CAMERA_TEMP_PATH;
    public static String PHOTO_TEMP_PATH;

    public static int nScreenW = 0;
    public static int nScreenH = 0;
    public static float nDensity = 0;

    public static String getSDPath() {
        initPath();
        return SD_PATH;
    }

    public static String getAppPath() {
        initPath();
        return APP_PATH;
    }

    public static String getTempPath() {
        initPath();
        return TEMP_PATH;
    }

    public static String getCameraTempPath() {
        initPath();
        return CAMERA_TEMP_PATH;
    }

    public static String getPhotoTempPath() {
        initPath();
        return PHOTO_TEMP_PATH;
    }

    private static void initPath() {
        if (SD_PATH != null) {
            return;
        }

        SD_PATH = SdcardUtil.getSDPath();

        if (SD_PATH == null) {
            return;
        }
        APP_PATH = SD_PATH + "/tuya/";
        TEMP_PATH = SD_PATH + "/tuya/.temp/";
        CAMERA_TEMP_PATH = TEMP_PATH + "camera.jpg";
        PHOTO_TEMP_PATH = TEMP_PATH + "photo.png";
    }

    public static void getScreenSize(Activity activity) {
        // 获取屏幕尺寸
        if (nScreenW == 0 || nScreenH == 0 || nDensity == 0) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            display.getMetrics(dm);
            nScreenW = dm.widthPixels;
            nScreenH = dm.heightPixels;
            nDensity = dm.density;// 得到设备的像素密度
        }
    }

}
