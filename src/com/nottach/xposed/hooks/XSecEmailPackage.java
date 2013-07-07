package com.nottach.xposed.hooks;

import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XSecEmailPackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XSecEmailPackage.prefs = prefs;
		XSecEmailPackage.classLoader = classLoader;

		if (prefs.getBoolean("disableExchangeLockSecurity", false)) {
			disableExchangeLockSecurity();
		}

	}

	private static void disableExchangeLockSecurity() {

		XposedHelpers.findAndHookMethod(Packages.EMAIL + ".SecurityPolicy",
				classLoader, "isActiveAdmin", new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return Boolean.valueOf(false);
					}
				});

	}

}
