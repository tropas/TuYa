/*
 * 版    权： 大肥与胖毛工作室
 * 创建人: d.cc
 * 创建时间: 2013-11-20
 */
package com.tuya.imagine.pip.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuya.baselib.widget.HorizontalListView;
import com.tuya.imagine.R;
import com.tuya.imagine.setting.Appsetting;

/**
 * 画中画的素材集合适配器
 * @author 胖毛
 * @date 2013-11-20
 */
public class PipPhotosAdapter extends BaseAdapter{
	
	private Context mContext;
	private int[] mDatas;
	private ItemView mItemView;
	
	public PipPhotosAdapter(Context context) {
		this.mContext = context;
	}
	
	public PipPhotosAdapter(Context context, int[] datas) {
		this.mContext = context;
		this.mDatas = datas;
	}

	@Override
	public int getCount() {
		
		return mDatas.length;
	}

	@Override
	public Object getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.view_pip_list_item, null);
			mItemView = new ItemView();
			mItemView.text = (TextView) convertView.findViewById(R.id.index);
			mItemView.view = (ImageView) convertView.findViewById(R.id.drawable);
			convertView.setTag(mItemView);
		}else{
			mItemView = (ItemView) convertView.getTag();
		}
		mItemView.text.setText(position+"");
		mItemView.view.setBackgroundResource(mDatas[position]);
		
		convertView.setLayoutParams(new HorizontalListView.LayoutParams(
				(int) (80 * Appsetting.nDensity), (int) (80 * Appsetting.nDensity)));

		return convertView;
	}
	
	
	private class ItemView{
		TextView text;
		ImageView view;
	}

}

