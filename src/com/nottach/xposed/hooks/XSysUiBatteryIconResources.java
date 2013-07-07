package com.nottach.xposed.hooks;

import android.content.res.XModuleResources;
import android.os.Build;

import com.nottach.xposed.R;
import com.nottach.xposed.R.array;
import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class XSysUiBatteryIconResources {

	static InitPackageResourcesParam resparam;
	static XModuleResources moduleResources;
	private static String batterySuffix;

	public static void doHook(XSharedPreferences prefs,
			InitPackageResourcesParam resparam, XModuleResources moduleResources) {
		XSysUiBatteryIconResources.resparam = resparam;
		XSysUiBatteryIconResources.moduleResources = moduleResources;
		String[] resNames = moduleResources
				.getStringArray(R.array.battery_drawable_res_names);
		batterySuffix = prefs.getString("selectedBatteryIcon", "Stock");
		if (batterySuffix.equals("Stock")) {
			return;
		}
		for (String string : resNames) {
			replaceBatteryIconsFromModule(string);
		}
		replaceXmls(new String[] { "stat_sys_battery",
				"stat_sys_battery_charge" });
		if (Build.DISPLAY.contains("BMEA")) {
			replaceTwXmls();
		}
	}

	private static void replaceTwXmls() {
		resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
				"tw_stat_sys_battery_red", moduleResources.fwd(moduleResources
						.getIdentifier(
								"stat_sys_battery_"
										+ batterySuffix.toLowerCase(),
								"drawable", Packages.NOTTACH_XPOSED)));
	}

	private static void replaceXmls(String[] xmls) {
		for (String string : xmls) {
			resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable", string,
					moduleResources.fwd(moduleResources.getIdentifier(string
							+ "_" + batterySuffix.toLowerCase(), "drawable",
							Packages.NOTTACH_XPOSED)));
			try {
				resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
						string + "_att", moduleResources.fwd(moduleResources
								.getIdentifier(
										string + "_"
												+ batterySuffix.toLowerCase(),
										"drawable", Packages.NOTTACH_XPOSED)));
			} catch (Throwable t) {
			}
		}
	}

	private static void replaceBatteryIconsFromModule(final String resName) {
		try {
			resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
					resName, moduleResources.fwd(moduleResources.getIdentifier(
							resName + "_" + batterySuffix.toLowerCase(),
							"drawable", Packages.NOTTACH_XPOSED)));
		} catch (Throwable t) {
			if (t.getMessage().contains(Packages.SYSTEM_UI + ":drawable/")) {
				resparam.res.addResource(moduleResources, moduleResources
						.getIdentifier(
								resName + "_" + batterySuffix.toLowerCase(),
								"drawable", Packages.NOTTACH_XPOSED));
			}
		}
	}
}
