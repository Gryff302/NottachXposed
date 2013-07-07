package com.nottach.xposed.hooks;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;

import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class XSecSettingsPackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XSecSettingsPackage.prefs = prefs;
		XSecSettingsPackage.classLoader = classLoader;

		if (prefs.getBoolean("hideWifiConnected", false)) {
			XposedHelpers.findAndHookMethod(Packages.SETTINGS
					+ ".wifi.WifiStatusReceiver", classLoader,
					"showConnectedNotification", Context.class, boolean.class,
					String.class, XC_MethodReplacement.DO_NOTHING);
		}

		if (prefs.getBoolean("hideBlockingModeIcon", false)) {
			XposedHelpers.findAndHookMethod(Packages.SETTINGS
					+ ".dormantmode.DormantModeNotiReceiver", classLoader,
					"notificationCreate", Context.class,
					XC_MethodReplacement.DO_NOTHING);
		}

		if (prefs.getBoolean("disableBluetoothScanDialog", false)) {
			disableBluetoothScanDialog();
		}

		if (prefs.getBoolean("disableTetherProvisioning", false)) {
			disableTetherProvisioning();
		}

	}

	private static void disableTetherProvisioning() {
		XposedHelpers.findAndHookMethod(Packages.SETTINGS + ".TetherSettings",
				classLoader, "isProvisioningNeeded",
				new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return (boolean) false;
					}
				});

		XposedHelpers.findAndHookMethod(Packages.SETTINGS
				+ ".wifi.mobileap.WifiApSwitchEnabler", classLoader,
				"isProvisioningNeeded", new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return (boolean) false;
					}
				});
	}

	private static void disableBluetoothScanDialog() {
		XposedHelpers.findAndHookMethod(Packages.SETTINGS
				+ ".bluetooth.BluetoothScanDialog", classLoader,
				"onPostCreate", Bundle.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						((android.app.Activity) param.thisObject).finish();
					}
				});
	}

}
