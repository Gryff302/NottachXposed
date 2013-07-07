package com.nottach.xposed.hooks;

import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class XSysUIFeaturePackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XSysUIFeaturePackage.prefs = prefs;
		XSysUIFeaturePackage.classLoader = classLoader;

		Class<?> classFeature = XposedHelpers.findClass(Packages.SYSTEM_UI
				+ ".statusbar.Feature", classLoader);
		try {
			XposedHelpers.setStaticBooleanField(classFeature,
					"mUseATTBatteryIcon", false);
		} catch (Throwable t) {
		}

		if (!prefs.getBoolean("useAnimatedBrightnessIcon", true)) {
			XposedHelpers.setStaticBooleanField(classFeature,
					"mUseAnimatedBrightnessIcon", false);
		}
		if (prefs.getBoolean("useAutoBrightnessDetail", false)) {
			XposedHelpers.setStaticBooleanField(classFeature,
					"mUseAutoBrightnessDetail", true);
		}
		if (prefs.getBoolean("removeGoogleNowFromRecents", false)) {
			XposedHelpers.setStaticBooleanField(classFeature,
					"mShowRecentGoogleNowButton", false);
		}
		if (prefs.getBoolean("hideWifiInAndOut", false)) {
			XposedHelpers.setStaticBooleanField(classFeature,
					"mHideWifiInAndOut", true);
		}
		if (prefs.getBoolean("persistDataIcon", false)) {
			XposedHelpers.setStaticBooleanField(classFeature,
					"mDataIconForCHN", true);
		}
		if (prefs.getString("nfcIcon", "AT&#38;T icon").equals("Global icon")) {
			XposedHelpers.setStaticBooleanField(classFeature, "mUseAttNfcIcon",
					false);
		}

		if (prefs.getBoolean("hideWirelessChargingPopUp", false)) {
			hideWirelessChargingPopUp();
		}

	}

	private static void hideWirelessChargingPopUp() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI + ".power.PowerUI",
				classLoader, "showWirelessChargingNotice", int.class,
				XC_MethodReplacement.DO_NOTHING);
	}
}
