/*
 * 版    权： 大肥与胖毛工作室
 * 创建人: d.cc
 * 创建时间: 2014-4-29
 */
package com.tuya.imagine.activity;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.tuya.baselib.share.ShareToAppListAdapter;
import com.tuya.baselib.util.StringUtil;
import com.tuya.imagine.MyConstant;
import com.tuya.imagine.R;
import com.tuya.imagine.app.BaseActivity;

/**
 * 
 * @author 胖毛
 * @date 2014-4-29
 */
public class SnsActivity extends BaseActivity implements OnClickListener {

	private String mTempPath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sns);
		mTempPath = this.getIntent().getStringExtra(MyConstant.EXTRA_SAVE_PATH);
		this.findViewById(R.id.btn_left).setOnClickListener(this);
		this.findViewById(R.id.btn_right).setOnClickListener(this);
		this.findViewById(R.id.btn_share2more).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.btn_save) {

		} else if (id == R.id.btn_left) {
			SnsActivity.this.finish();
		} else if (id == R.id.btn_right) {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(this, ImagineActivity.class);
			this.startActivity(intent);
		} else if (id == R.id.btn_share2more) {
			if (!StringUtil.isEmptyString(mTempPath)) {
				shareMore(new File(mTempPath));
			}
		}
	}

	public void shareMore(File newPath) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				SnsActivity.this);
		final ShareToAppListAdapter chooseAppAdapter = new ShareToAppListAdapter(
				SnsActivity.this, "", Uri.fromFile(newPath));
		alertDialog.setAdapter(chooseAppAdapter,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SnsActivity.this
								.startActivity((Intent) chooseAppAdapter
										.getItem(which));
					}
				}).show();
	}

}
