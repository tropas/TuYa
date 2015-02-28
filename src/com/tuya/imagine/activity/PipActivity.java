package com.tuya.imagine.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.tuya.baselib.util.BitmapUtil;
import com.tuya.baselib.util.FileUtil;
import com.tuya.baselib.widget.HorizontalListView;
import com.tuya.baselib.widget.ThreadLoadingDialog;
import com.tuya.imagine.MyConstant;
import com.tuya.imagine.MyData;
import com.tuya.imagine.R;
import com.tuya.imagine.app.BaseActivity;
import com.tuya.imagine.pip.adapter.PipPhotosAdapter;
import com.tuya.imagine.pip.data.PipFrameProperties;
import com.tuya.imagine.pip.view.PIPView;
import com.tuya.imagine.setting.Appsetting;
import com.tuya.imagine.utils.AppUtil;
import com.tuya.imagine.utils.ChoosePicUtil;

public class PipActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private PIPView showView = null;
	private HorizontalListView listView = null;
	private PipPhotosAdapter mAdapter;

	private Bitmap mBmpSrc;
	private static int FrameID = 0;

	/* 拍照图片的最终路径uri */
	private Uri mCameraUri = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pip);
		showView = (PIPView) findViewById(R.id.pipView);
		findViewById(R.id.btn_return).setOnClickListener(this);
		findViewById(R.id.btn_save).setOnClickListener(this);
		findViewById(R.id.btn_camera).setOnClickListener(this);
		findViewById(R.id.btn_photo).setOnClickListener(this);

		MyData.PicPath = getIntent().getStringExtra(MyData.EXTRA_PIC_PATH);
		mBmpSrc = BitmapUtil.getBitmap(MyData.PicPath, Config.ARGB_8888, 1200);
		listView = (HorizontalListView) findViewById(R.id.pip_frame_list);
		listView.setOnItemClickListener(this);
		setAdapter();
		if (!BitmapUtil.isValid(mBmpSrc)) {
			mBmpSrc = BitmapUtil.getBitmap(MyData.PicPath, Config.ARGB_8888, 1200);

		}
		showView.initData(PipFrameProperties.pipFrames[FrameID], mBmpSrc);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void setAdapter() {
		String[] texts = null;
		TypedArray thumbs = this.getResources().obtainTypedArray(R.array.pip_type1_img);
		int length = thumbs.length();
		int[] imgs = new int[length];
		for (int i = 0; i < length; i++) {
			imgs[i] = thumbs.getResourceId(i, 0);
		}
		if (mAdapter == null) {
			mAdapter = new PipPhotosAdapter(this, imgs);
			listView.setAdapter(mAdapter);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.btn_return) {
			this.finish();
		} else if (id == R.id.btn_save) {
			MyData.ShowBitmap = showView.getSavePic();
			BitmapUtil.safeRecycled(mBmpSrc);
			new ThreadLoadingDialog(PipActivity.this) {

				@Override
				public void process() {
					String path = AppUtil.saveBitmap2SD(MyData.ShowBitmap);
					// PJData.setSaveBitmapPath(path);
					Intent intent = new Intent(PipActivity.this, SnsActivity.class);
					intent.putExtra(MyConstant.EXTRA_SAVE_PATH, path);
					PipActivity.this.startActivity(intent);
				}
			}.show();
		} else if (id == R.id.btn_camera) {
			File f = new File(Appsetting.getCameraTempPath());
			if (f.exists()) {
				f.delete();
			}
			mCameraUri = Uri.fromFile(f);
			ChoosePicUtil.gotoCamera(PipActivity.this, mCameraUri);
		} else if (id == R.id.btn_photo) {
			ChoosePicUtil.gotoPhoto(PipActivity.this);
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
			mBmpSrc = BitmapUtil.getBitmap(MyData.PicPath, Config.ARGB_8888, 1200);
			if (!BitmapUtil.isValid(mBmpSrc)) {
				mBmpSrc = BitmapUtil.getBitmap(MyData.PicPath, Config.ARGB_8888, 1200);

			}
			showView.changePic(mBmpSrc);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// if(showView != null)
		// showView.removeAllViews();
		// showView.releaseView();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// mAdapter.setSelectionImageId(position);
		mAdapter.notifyDataSetChanged();
		showView.ChangeFrame(PipFrameProperties.pipFrames[position]);
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

}
