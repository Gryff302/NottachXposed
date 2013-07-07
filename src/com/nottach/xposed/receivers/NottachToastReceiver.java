package com.nottach.xposed.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NottachToastReceiver extends BroadcastReceiver {

	private static final String SHOW_TOAST = "com.nottach.xposed.action.SHOW_TOAST";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(SHOW_TOAST)) {
			Toast.makeText(context,
					"Killed: " + intent.getStringExtra("processName"),
					Toast.LENGTH_SHORT).show();

		}
	}
}
