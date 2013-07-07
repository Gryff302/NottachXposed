package com.nottach.xposed.hooks;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XSecLauncherPackage {

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {
		if (prefs.getBoolean("opaqueLauncherStatusbar", false)) {
			final Class<?> classLauncher = XposedHelpers.findClass(
					"com.android.launcher2.Launcher", classLoader);
			XposedHelpers.findAndHookMethod(classLauncher, "onResume",
					new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param)
								throws Throwable {
							XposedHelpers.setStaticBooleanField(classLauncher,
									"SYSTEMUI_TRANSPARENCY", true);

						}
					});
		}
	}

}
