package com.nottach.xposed.hooks;

import de.robv.android.xposed.XSharedPreferences;

public class XSysUiPackage {

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XSysUIFeaturePackage.doHook(prefs, classLoader);

		XSysUiRecentsPackage.doHook(prefs, classLoader);
		
		XSysUiStatusBarClockPackage.doHook(prefs, classLoader);

		XSysUiNotificationPanelClockPackage.doHook(prefs, classLoader);

		XSysUIQuickSettingsPackage.doHook(prefs, classLoader);

		XSysUiBatteryPackage.doHook(prefs, classLoader);

		XSysUiStatusBarCarrierPackage.doHook(prefs, classLoader);

		XSysUiSignalPackage.doHook(prefs, classLoader);

		XSysUiSmartAlarmPackage.doHook(prefs, classLoader);

		XSysUiNotificationPanelPackage.doHook(prefs, classLoader);

	}

}
