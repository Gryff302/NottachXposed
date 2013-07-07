package com.nottach.xposed.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nottach.xposed.activities.NottachTorchActivity;

public class NottachTorchReceiver extends BroadcastReceiver {

	private static final String TORCH = "com.nottach.xposed.action.TORCH";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(TORCH)) {

			Intent torchIntent = new Intent(context, NottachTorchActivity.class);
			torchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(torchIntent);

		}
	}
}
