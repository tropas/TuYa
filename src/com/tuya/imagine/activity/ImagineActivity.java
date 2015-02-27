package com.tuya.imagine.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.tuya.baselib.util.FileUtil;
import com.tuya.imagine.MyConstant;
import com.tuya.imagine.MyData;
import com.tuya.imagine.R;
import com.tuya.imagine.app.BaseActivity;
import com.tuya.imagine.setting.Appsetting;
import com.tuya.imagine.utils.ChoosePicUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class ImagineActivity extends BaseActivity implements OnClickListener {

	/* 拍照图片的最终路径uri */
	private Uri mCameraUri = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MobclickAgent.updateOnlineConfig(ImagineActivity.this);
		UmengUpdateAgent.update(ImagineActivity.this);
		findViewById(R.id.btn_praise).setOnClickListener(this);
		findViewById(R.id.btn_camera).setOnClickListener(this);
		findViewById(R.id.btn_photo).setOnClickListener(this);
		findViewById(R.id.btn_setting).setOnClickListener(this);
	}

	@Override
	protected void onResume() {

		super.onResume();

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.btn_camera) {// 拍照

			File f = new File(Appsetting.getCameraTempPath());
			if (f.exists()) {
				f.delete();
			}
			mCameraUri = Uri.fromFile(f);
			ChoosePicUtil.gotoCamera(this, mCameraUri);
		} else if (id == R.id.btn_photo) {// 相册

			ChoosePicUtil.gotoPhoto(this);

		} else if (id == R.id.btn_setting) {// 设置
			Intent intent = new Intent(ImagineActivity.this,
					SettingActivity.class);
			ImagineActivity.this.startActivity(intent);
		} else if (id == R.id.btn_praise) {// 称赞
			givePraise();
		}
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_CANCELED) {
			// 取消不做任何事情
		} else if (resultCode == Activity.RESULT_OK) {
			// 返回正常
			String filePath = null;
			if (data == null) {
				if (requestCode == ChoosePicUtil.FROM_CAMERA) {
					// 来自摄像头，需要从自己保存的uri中获取路径
					filePath = getCameraPath();
				} else {
					// 来自相册，但是选取获取的图片为空
					return;
				}
			} else {

				if (requestCode != ChoosePicUtil.FROM_CAMERA) {
					// 不是来自摄像头的数据
					filePath = FileUtil.getPathFromUri(this, data.getData());

					if (!FileUtil.isImageFile(filePath)) {
						// 无效的文件类型
						Toast.makeText(this, "无效图片！", Toast.LENGTH_LONG).show();
						return;
					} else {

					}
				} else {
					// 来自摄像头的数据，
					filePath = getCameraPath();
				}

			}

			// 校验文件是否存在
			boolean isExist = false;
			if (filePath != null) {
				File lFile = new File(filePath);
				if (lFile != null)
					isExist = lFile.exists();
			}

			if (!isExist) {
				Toast.makeText(this, "无法加载该图片!", Toast.LENGTH_SHORT).show();
				return;
			}

			MyData.PicPath = filePath;

			// 返回数据不为空
			if (requestCode == ChoosePicUtil.FROM_CAMERA
					|| requestCode == ChoosePicUtil.FROM_PHOTO) {
				Intent intent = new Intent();
				intent.setClass(this, PipActivity.class);
				intent.putExtra(MyData.EXTRA_PIC_PATH, MyData.PicPath);
				if (requestCode == ChoosePicUtil.FROM_PHOTO) {
					intent.putExtra(MyConstant.IS_FROM_CAMERA, false);
				} else if (requestCode == ChoosePicUtil.FROM_CAMERA) {
					intent.putExtra(MyConstant.IS_FROM_CAMERA, true);
				} else {
					// do nothing.
				}
				startActivity(intent);
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 获取摄像头拍摄完成后，文件保存的路径
	 * 
	 * @return
	 */
	private String getCameraPath() {
		String finalFilePath = mCameraUri.getPath();
		File file = new File(finalFilePath);

		if (!file.exists()) {
			// 保存失败
			file = null;
			finalFilePath = null;
			return null;
		} else {
			return finalFilePath;
		}
	}

	private void deleteTempFile() {
		// TODO Auto-generated method stub
		String tempPath = Appsetting.getTempPath();

		File f = new File(tempPath);
		if (f.isDirectory()) {
			File files[] = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (!files[i].equals(new File(Appsetting.getCameraTempPath())))
					files[i].delete();
			}
		}
	}

	// 给个好评
	private void givePraise() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=com.tuya.imagine"));
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MobclickAgent.flush(this);
	}

}
