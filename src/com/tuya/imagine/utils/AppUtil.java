/*
 * 版    权： 大肥与胖毛工作室
 * 创建人: d.cc
 * 创建时间: 2014-4-29
 */
package com.tuya.imagine.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;

import com.tuya.baselib.util.BitmapUtil;
import com.tuya.baselib.util.FileUtil;
import com.tuya.baselib.util.SaveUtil;
import com.tuya.imagine.MyData;
import com.tuya.imagine.setting.Appsetting;

/**
 * 
 * @author 胖毛
 * @date 2014-4-29
 */
public class AppUtil {

	public static String saveBitmap2SD(Bitmap bmp) {

		String savePath = Appsetting.getAppPath();

		File f = new File(savePath);
		if (!f.exists()) {
			f.mkdirs();
		}

		String savePicPath = "";

		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		String date = sDateFormat.format(new Date());
		savePicPath = savePath + date + ".png";

		boolean isSuccend = SaveUtil.saveBitmap2SD(savePicPath, bmp, 1);
		if (isSuccend) {
			return savePicPath;
		} else {
			return null;
		}

	}

	public static boolean saveBitmap2TempPath(Bitmap bmp) {

		String tempPath = Appsetting.getPhotoTempPath();
		if (FileUtil.isFileExist(tempPath)) {
			FileUtil.createFile(tempPath);
		}
		File f = new File(tempPath);
		return SaveUtil.saveBitmap2SD(f, bmp, 1);
	}

	public static boolean saveBitmap2SD(Bitmap bmp, String path) {

		if (FileUtil.isFileExist(path)) {
			FileUtil.createFile(path);
		}
		File f = new File(path);
		return SaveUtil.saveBitmap2SD(f, bmp, 1);
	}

}
