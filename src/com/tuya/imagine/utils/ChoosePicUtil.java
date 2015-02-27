/*
 * 版    权： 大肥与胖毛工作室
 * 创建人: d.cc
 * 创建时间: 2014-5-11
 */
package com.tuya.imagine.utils;

import java.io.File;

import com.tuya.imagine.setting.Appsetting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 
 * @author 胖毛
 * @date 2014-5-11
 */
public class ChoosePicUtil {

	/* 拍照请求码 */
	public static final int FROM_CAMERA = 0x01;
	/* 选取图片请求码 */
	public static final int FROM_PHOTO = 0x02;

	public static void gotoCamera(Activity activity, Uri uri) {

		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);// 保存到sd卡
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		activity.startActivityForResult(intent, FROM_CAMERA);
	}

	public static void gotoPhoto(Activity activity) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(intent, FROM_PHOTO);
	}

}
