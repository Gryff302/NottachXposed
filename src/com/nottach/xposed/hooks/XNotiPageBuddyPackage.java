package com.nottach.xposed.hooks;

import android.content.Context;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XNotiPageBuddyPackage {

	public static void doHook(String packageName, XSharedPreferences prefs,
			ClassLoader classLoader) {

		if (prefs.getBoolean("hideHeadsetAppsNotification", false)) {
			if (prefs.getBoolean("hideHeadsetNotificationIcon", false)) {
				XposedHelpers.findAndHookMethod(packageName + ".PageBuddyNoti",
						classLoader, "notificationSend", Context.class,
						int.class, boolean.class,
						XC_MethodReplacement.DO_NOTHING);
			} else {
				XposedHelpers.findAndHookMethod(packageName + ".PageBuddyNoti",
						classLoader, "notificationSend", Context.class,
						int.class, boolean.class, new XC_MethodHook() {
							@Override
							protected void beforeHookedMethod(
									MethodHookParam param) throws Throwable {

								param.args[2] = false;

							}
						});
			}
		}
	}

}
