package com.nottach.xposed.activities;

import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.nottach.xposed.adapters.AppsAdapter;
import com.nottach.xposed.notifications.RebootNotification;

public class NottachAppChooser extends ListActivity {

	public static final String APP_URI = "app_uri";
	public static final String APP_LABEL = "app_label";

	private ListView lvApps;
	private PackageManager packageManager;
	private String prefUriString;
	private String prefLabelString;
	private List<ResolveInfo> appsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		packageManager = getPackageManager();

		Bundle b = getIntent().getBundleExtra("pref");
		prefUriString = b.getString(APP_URI);
		prefLabelString = b.getString(APP_LABEL);

		lvApps = getListView();

		new LoadActivitiesTask().execute();

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Intent i = new Intent();
		i.setComponent(new ComponentName(
				appsList.get(position).activityInfo.applicationInfo.packageName,
				appsList.get(position).activityInfo.name));

		PreferenceManager.getDefaultSharedPreferences(NottachAppChooser.this)
				.edit().putString(prefUriString, i.toURI()).commit();
		PreferenceManager
				.getDefaultSharedPreferences(NottachAppChooser.this)
				.edit()
				.putString(
						prefLabelString,
						appsList.get(position).loadLabel(packageManager)
								.toString()).commit();

		if (prefLabelString.equals("doubleHomeClickApplicationLabel")) {
			PreferenceManager
					.getDefaultSharedPreferences(NottachAppChooser.this).edit()
					.putBoolean("doubleHomeClickIsRecents", false).commit();
			RebootNotification.notify(this, RebootNotification.getNumber() + 1, false);
		}

		Toast.makeText(
				NottachAppChooser.this,
				"Shortcut changed: "
						+ appsList.get(position).loadLabel(packageManager)
								.toString(), Toast.LENGTH_SHORT).show();
		finish();
	}

	class LoadActivitiesTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);

			Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
			if (!PreferenceManager.getDefaultSharedPreferences(
					NottachAppChooser.this).getBoolean(
					"appChooserShowAllActivities", false)) {
				launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			}
			appsList = packageManager.queryIntentActivities(launcherIntent, 0);
		}

		@Override
		protected Void doInBackground(Void... params) {

			Collections.sort(appsList, new ResolveInfo.DisplayNameComparator(
					packageManager));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			setProgressBarIndeterminateVisibility(false);
			lvApps.setAdapter(new AppsAdapter(NottachAppChooser.this,
					packageManager, appsList));
		}

	}

}
