package com.nottach.xposed.hooks;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XGooWalletPackage {

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		if (prefs.getBoolean("applyGoogleWalletHack", false)) {
			applyGoogleWalletHack(classLoader);
		}

	}

	private static void applyGoogleWalletHack(ClassLoader classLoader) {

		XC_MethodReplacement truereplacer = new XC_MethodReplacement() {
			protected Object replaceHookedMethod(
					XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
					throws Throwable {
				return Boolean.valueOf("True");

			}
		};

		XC_MethodReplacement falsereplacer = new XC_MethodReplacement() {
			protected Object replaceHookedMethod(
					XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
					throws Throwable {
				return Boolean.valueOf("False");

			}
		};
		XposedHelpers
				.findAndHookMethod(
						"com.google.android.apps.wallet.util.DeviceCapabilityManagerImpl",
						classLoader, "checkWhitelistEntries", new Object[] {
								String.class, String[].class, truereplacer });
		XposedHelpers
				.findAndHookMethod(
						"com.google.android.apps.wallet.datamanager.local.UserInfoManagerImpl",
						classLoader, "isWalletAllowedForUserInCountry",
						new Object[] { truereplacer });

		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = XposedHelpers.findClass(
				"com.google.android.apps.wallet.security.EnvironmentInfo",
				classLoader);
		arrayOfObject[1] = falsereplacer;
		XposedHelpers
				.findAndHookMethod(
						"com.google.android.apps.wallet.security.EnvironmentProperty$1",
						classLoader, "checkDevice", arrayOfObject);
	}
}
