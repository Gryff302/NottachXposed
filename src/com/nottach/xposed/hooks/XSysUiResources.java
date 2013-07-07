package com.nottach.xposed.hooks;

import android.content.res.XModuleResources;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class XSysUiResources {

	public static void doHook(XSharedPreferences prefs,
			InitPackageResourcesParam resparam, XModuleResources moduleResources) {

		XSysUiBatteryIconResources.doHook(prefs, resparam, moduleResources);

		XSysUiStatusIconResources.doHook(prefs, resparam, moduleResources);

		XSysUiStatusBarResources.doHook(prefs, resparam, moduleResources);

		XSysUiQuickSettingsResources.doHook(prefs, resparam, moduleResources);

		XSysUiNotificationPanelResources.doHook(prefs, resparam,
				moduleResources);
	}
}
