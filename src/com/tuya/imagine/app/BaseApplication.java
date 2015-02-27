/*
 * 版    权： 大肥与胖毛工作室
 * 创建人: d.cc
 * 创建时间: 2013-11-17
 */
package com.tuya.imagine.app;

import com.tuya.imagine.MyData;
import com.umeng.analytics.MobclickAgent;

import android.app.Application;

/**
 * 
 * @author 胖毛
 * @date 2013-11-17
 */
public class BaseApplication extends Application {

	private static BaseApplication sBaseApplcation = null;

	@Override
	public void onCreate() {
		super.onCreate();
		// 初始化自身静态变量
		sBaseApplcation = this;
		MobclickAgent.updateOnlineConfig(this);
		new Thread(new Runnable() {
            
            @Override
            public void run() {
                MyData.getLocalQuality();
            }
        }).start();
	}

	public static Application getApplication() {
		return sBaseApplcation;
	}
}
