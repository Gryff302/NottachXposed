package com.nottach.xposed.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nottach.xposed.activities.NottachRebootActivity;
import com.nottach.xposed.notifications.RebootNotification;

public class NottachRebootReceiver extends BroadcastReceiver {

	private static final String REBOOT_DEVICE = "com.nottach.xposed.action.REBOOT_DEVICE";
	private static final String SOFT_REBOOT_DEVICE = "com.nottach.xposed.action.SOFT_REBOOT_DEVICE";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(REBOOT_DEVICE) || action.equals(SOFT_REBOOT_DEVICE)) {
			RebootNotification.cancel(context);
			Intent rebootIntent = new Intent(context, NottachRebootActivity.class);
			Bundle b = new Bundle();
			if (action.equals(REBOOT_DEVICE)) {
				b.putBoolean("isSoft", false);
			} else {
				b.putBoolean("isSoft", true);
			}
			rebootIntent.putExtras(b);
			rebootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(rebootIntent);
		}
	}
}
