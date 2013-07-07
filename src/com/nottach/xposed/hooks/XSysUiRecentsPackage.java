package com.nottach.xposed.hooks;

import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.nottach.xposed.activities.NottachXposed;
import com.nottach.xposed.receivers.NottachLaunchReceiver;
import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XSysUiRecentsPackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XSysUiRecentsPackage.prefs = prefs;
		XSysUiRecentsPackage.classLoader = classLoader;

		if (prefs.getInt("longHomePressBehaviour",
				NottachXposed.RECENTS_THUMBNAIL) == NottachXposed.RECENTS_LAUNCH_APP) {
			setHomeLongPressApplication();
		}

	}

	private static void setHomeLongPressApplication() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.BaseStatusBar$H", classLoader, "handleMessage",
				Message.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						Message message = (Message) param.args[0];
						if (message.what == 1020) {
							((Context) XposedHelpers.getObjectField(
									XposedHelpers
											.getSurroundingThis(param.thisObject),
									"mContext"))
									.sendBroadcast(new Intent(
											NottachLaunchReceiver.START_HOME_LONG_ACTIVITY));
							param.setResult(null);
						}
					}
				});

	}
}
