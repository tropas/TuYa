/*
 * 版    权： 大肥与胖毛工作室
 * 创建人: d.cc
 * 创建时间: 2014-5-20
 */

package com.tuya.imagine.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tuya.baselib.util.NetUtil;
import com.tuya.imagine.MyData;
import com.tuya.imagine.R;
import com.tuya.imagine.app.BaseActivity;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

/**
 * 
 * @author 胖毛
 * @date 2014-5-20
 */
public class SettingActivity extends BaseActivity implements OnClickListener {

    private TextView mQualityTextView;
    private int mQuality = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        this.findViewById(R.id.btn_left).setOnClickListener(this);
        this.findViewById(R.id.btn_huazhi).setOnClickListener(this);
        this.findViewById(R.id.btn_save_auto).setOnClickListener(this);
        this.findViewById(R.id.btn_save_path).setOnClickListener(this);
        this.findViewById(R.id.btn_check_update).setOnClickListener(this);
        this.findViewById(R.id.btn_about_tuya).setOnClickListener(this);
        setQuality();
    }

    private void setQuality() {
        mQualityTextView = (TextView) findViewById(R.id.text_quality);
        mQuality = MyData.PicQuality;
        try {
            mQualityTextView.setText(getResources().getStringArray(R.array.save_picsize_string)[mQuality]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.btn_left) {
            this.finish();
        } else if (id == R.id.btn_huazhi) {// 画质设置
            choosePicQuality(mQuality);
        } else if (id == R.id.btn_save_auto) {// 是否自动保存

        } else if (id == R.id.btn_save_path) {// 设置保存路径

        } else if (id == R.id.btn_check_update) {// 检查更新
            checkUpdate();
        } else if (id == R.id.btn_about_tuya) {// 关于图丫

        }
    }

    private void checkUpdate() {
        int netState = NetUtil.checkNetConnection(SettingActivity.this);
        if (netState != NetUtil.OK) {
            NetUtil.turnIntoNetSetting(this, netState, false);
            return;
        }
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                case 0: // has update
                    UmengUpdateAgent.showUpdateDialog(SettingActivity.this, updateInfo);
                    break;
                case 1: // has no update
                    Toast.makeText(SettingActivity.this, "没有更新", Toast.LENGTH_SHORT).show();
                    break;
                case 2: // none wifi
                    Toast.makeText(SettingActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                    break;
                case 3: // time out
                    Toast.makeText(SettingActivity.this, "超时", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        });
    }

    /**
     * 选择图片的精细程度
     * 
     * @param
     * @return boolean 是否改变
     */
    private void choosePicQuality(int selectedItem) {
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.setting_quality)).setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(getResources().getStringArray(R.array.save_picsize_string), selectedItem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mQuality != which) {
                            mQuality = which;
                            mQualityTextView.setText(getResources().getStringArray(R.array.save_picsize_string)[which]);
                            MyData.setPicQuality(mQuality);
                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UmengUpdateAgent.setUpdateListener(null);
    }

}
