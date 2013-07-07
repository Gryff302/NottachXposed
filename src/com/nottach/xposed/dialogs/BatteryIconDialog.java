package com.nottach.xposed.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nottach.xposed.R;
import com.nottach.xposed.adapters.BatteryIconAdapter;
import com.nottach.xposed.utils.Packages;

public class BatteryIconDialog extends DialogFragment {

	private Dialog dialog;

	public BatteryIconDialog() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final String[] batteries = getResources().getStringArray(
				R.array.battery_icon_values);
		Drawable[] batteryDrawables = new Drawable[batteries.length];
		for (int i = 0; i < batteries.length; i++) {
			batteryDrawables[i] = getResources().getDrawable(
					getResources()
							.getIdentifier(
									"stat_sys_battery_65_"
											+ batteries[i].toLowerCase(),
									"drawable", Packages.NOTTACH_XPOSED));
		}

		BatteryIconAdapter adapter = new BatteryIconAdapter(getActivity(),
				batteries, batteryDrawables);

		ListView listView = new ListView(getActivity());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				PreferenceManager.getDefaultSharedPreferences(getActivity())
						.edit()
						.putString("selectedBatteryIcon", batteries[arg2])
						.commit();
				dialog.dismiss();
			}

		});

		dialog = builder.setCancelable(true).setTitle(R.string.battery_icon)
				.setView(listView).create();
		return dialog;
	}

}
