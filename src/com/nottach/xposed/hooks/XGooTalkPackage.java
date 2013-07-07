package com.nottach.xposed.hooks;

import android.content.ContentResolver;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XGooTalkPackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XGooTalkPackage.prefs = prefs;
		XGooTalkPackage.classLoader = classLoader;

		if (prefs.getBoolean("enableVcOverCellular", false)) {
			enableVcOverCellular();
		}

	}

	private static void enableVcOverCellular() {

		XposedHelpers.findAndHookMethod("com.google.android.gsf.Gservices",
				classLoader, "getBoolean", ContentResolver.class, String.class,
				boolean.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						String s = (String) param.args[1];
						if (s.equals("gtalk_vc_wifi_only")) {
							param.setResult(Boolean.FALSE);
						}
					}
				});

	}

}
