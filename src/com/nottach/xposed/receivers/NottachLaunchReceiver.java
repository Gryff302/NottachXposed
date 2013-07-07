package com.nottach.xposed.receivers;

import java.net.URISyntaxException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nottach.xposed.activities.NottachXposed;

public class NottachLaunchReceiver extends BroadcastReceiver {

	public static final String START_CLOCK_ACTIVITY = "com.nottach.xposed.action.START_CLOCK_ACTIVITY";
	public static final String START_CLOCK_LONG_ACTIVITY = "com.nottach.xposed.action.START_CLOCK_LONG_ACTIVITY";
	public static final String START_HOME_LONG_ACTIVITY = "com.nottach.xposed.action.START_HOME_LONG_ACTIVITY";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(START_CLOCK_ACTIVITY)) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			String intentUri = prefs.getString("clockDateApplicationUri", "");
			Intent i = null;
			if (intentUri == null || intentUri.equals("")) {
				i = new Intent(context, NottachXposed.class);
			} else {
				try {
					i = Intent.parseUri(intentUri, 0);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		} else if (action.equals(START_CLOCK_LONG_ACTIVITY)) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			String intentUri = prefs.getString("clockDateLongApplicationUri",
					"");
			Intent i = null;
			if (intentUri == null || intentUri.equals("")) {
				i = new Intent(context, NottachXposed.class);
			} else {
				try {
					i = Intent.parseUri(intentUri, 0);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		} else if (action.equals(START_HOME_LONG_ACTIVITY)) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			String intentUri = prefs.getString("homeLongApplicationUri", "");
			Intent i = null;
			if (intentUri == null || intentUri.equals("")) {
				i = new Intent(context, NottachXposed.class);
			} else {
				try {
					i = Intent.parseUri(intentUri, 0);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}
}
