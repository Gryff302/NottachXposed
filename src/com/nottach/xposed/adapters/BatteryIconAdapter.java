package com.nottach.xposed.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nottach.xposed.R;

public class BatteryIconAdapter extends BaseAdapter {

	private Context context;
	private String[] batteryNames;
	private Drawable[] batteryDrawables;

	public BatteryIconAdapter(Context context, String[] batteries,
			Drawable[] batteryDrawables2) {
		this.context = context;
		this.batteryNames = batteries;
		this.batteryDrawables = batteryDrawables2;

	}

	@Override
	public int getCount() {
		return batteryNames.length;
	}

	@Override
	public Object getItem(int position) {
		return batteryNames[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = LayoutInflater.from(context).inflate(
				R.layout.item_battery_icon, null);
		TextView tv = (TextView) v.findViewById(R.id.tvBatteryIcon);
		tv.setText(batteryNames[position]);
		ImageView iv = (ImageView) v.findViewById(R.id.ivBatteryIcon);
		iv.setImageDrawable(batteryDrawables[position]);
		return v;
	}
}
