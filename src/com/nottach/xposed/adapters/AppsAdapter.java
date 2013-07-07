package com.nottach.xposed.adapters;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nottach.xposed.R;

public class AppsAdapter extends BaseAdapter {

	private Context context;
	private List<ResolveInfo> appsList;
	private PackageManager pm;

	public AppsAdapter(Context context, PackageManager pm,
			List<ResolveInfo> appsList) {
		this.context = context;
		this.pm = pm;
		this.appsList = appsList;
	}

	@Override
	public int getCount() {
		return appsList.size();
	}

	@Override
	public Object getItem(int position) {
		return appsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder h;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_app, parent, false);
			h = new ViewHolder();
			convertView.setTag(h);
		} else {
			h = (ViewHolder) convertView.getTag();
		}

		h.icon = (ImageView) convertView.findViewById(R.id.ivAppIcon);
		h.icon.setImageDrawable(appsList.get(position).loadIcon(pm));

		h.appActivity = (TextView) convertView.findViewById(R.id.tvAppActivity);
		h.appActivity.setText(appsList.get(position).loadLabel(pm));
		h.appPackage = (TextView) convertView.findViewById(R.id.tvAppPackage);
		h.appPackage.setText(appsList.get(position).activityInfo.packageName);

		return convertView;
	}

	class ViewHolder {
		ImageView icon;
		TextView appActivity;
		TextView appPackage;
	}

}
