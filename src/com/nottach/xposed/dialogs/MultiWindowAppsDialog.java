package com.nottach.xposed.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.nottach.xposed.R;

public class MultiWindowAppsDialog extends DialogFragment {

	private AlertDialog dialog;
	private PackageManager packageManager;
	private List<ResolveInfo> cleanedAppsList;
	private CharSequence[] appsLabelArray;
	private boolean[] appsCheckedArray;

	public MultiWindowAppsDialog() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
		launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		packageManager = getActivity().getPackageManager();
		List<ResolveInfo> allAppsList = packageManager.queryIntentActivities(
				launcherIntent, 0);
		List<String> defaultAppsList = Arrays.asList(getResources()
				.getStringArray(R.array.config_multiWindowSupportAppList));
		cleanedAppsList = new ArrayList<ResolveInfo>();
		for (int i = 0; i < allAppsList.size(); i++) {
			if (!defaultAppsList
					.contains(allAppsList.get(i).activityInfo.packageName)) {
				cleanedAppsList.add(allAppsList.get(i));
			}
		}
		Collections.sort(cleanedAppsList,
				new ResolveInfo.DisplayNameComparator(packageManager));
		appsLabelArray = new CharSequence[cleanedAppsList.size()];
		appsCheckedArray = new boolean[cleanedAppsList.size()];
		String[] selectedApps = PreferenceManager
				.getDefaultSharedPreferences(getActivity())
				.getString("selectedMwApps", "").split(";");
		List<String> selectedAppsList = new ArrayList<String>(
				selectedApps.length);
		selectedAppsList.addAll(Arrays.asList(selectedApps));

		for (int i = 0; i < cleanedAppsList.size(); i++) {
			appsLabelArray[i] = cleanedAppsList.get(i)
					.loadLabel(packageManager);
			if (selectedAppsList
					.contains(cleanedAppsList.get(i).activityInfo.packageName)) {
				appsCheckedArray[i] = true;
			} else {
				appsCheckedArray[i] = false;
			}
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		OnMultiChoiceClickListener listener = new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which,
					boolean isChecked) {
				appsCheckedArray[which] = isChecked;
			}
		};
		dialog = builder
				.setMultiChoiceItems(appsLabelArray, appsCheckedArray, listener)
				.setCancelable(true)
				.setTitle("Multi-Window Apps")
				.setPositiveButton(R.string.apply, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						StringBuilder stringBuilder = new StringBuilder();
						for (int i = 0; i < appsCheckedArray.length; i++) {
							if (appsCheckedArray[i] == true) {
								stringBuilder.append(cleanedAppsList.get(i).activityInfo.packageName
										+ ";");
							}
						}

						PreferenceManager
								.getDefaultSharedPreferences(getActivity())
								.edit()
								.putString(
										"selectedMwApps",
										((stringBuilder.length() > 1) ? stringBuilder
												.toString()
												.substring(
														0,
														stringBuilder.length() - 1)
												: "")).commit();

					}
				})
				.setNegativeButton(android.R.string.cancel,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();

		return dialog;
	}

}
