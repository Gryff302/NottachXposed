package com.nottach.xposed.activities;

import com.nottach.xposed.R;
import com.nottach.xposed.R.string;
import com.nottach.xposed.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

public class NottachRebootActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean soft = getIntent().getExtras().getBoolean("isSoft", false);
		try {
			if (soft) {
				softRebootDevice();
			} else {
				rebootDevice();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void softRebootDevice() throws Throwable {
		Utils.closeStatusBar(this);
		Utils.softReboot(this);
	}

	private void rebootDevice() throws Throwable {
		Utils.closeStatusBar(this);
		ProgressDialog.show(this, "",
				getResources().getString(R.string.rebooting));
		Utils.reboot(this);
	}

}
