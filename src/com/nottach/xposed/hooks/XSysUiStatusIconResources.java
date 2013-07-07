package com.nottach.xposed.hooks;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.nottach.xposed.R;
import com.nottach.xposed.R.array;
import com.nottach.xposed.R.layout;
import com.nottach.xposed.R.string;
import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class XSysUiStatusIconResources {

	static final String[] sysUiStatusDrawables = new String[] {
			"stat_notify_image", "stat_notify_image_error", "stat_notify_more",
			"stat_notify_roaming_vzw", "stat_notify_slowcharging",
			"stat_sys_alarm", "stat_sys_data_bluetooth",
			"stat_sys_data_bluetooth_connected", "stat_sys_gps_acquiring",
			"stat_sys_location_e911_2", "stat_sys_location_on_2",
			"stat_sys_no_sim", "stat_sys_no_sim_cu", "stat_sys_ringer_silent",
			"stat_sys_ringer_vibrate", "stat_sys_roaming_cdma_0",
			"stat_sys_sync", "stat_sys_sync_error",
			"tw_indicator_smartstay_fail" };
	private static XSharedPreferences prefs;
	private static InitPackageResourcesParam resparam;
	private static XModuleResources moduleResources;

	public static void doHook(final XSharedPreferences prefs,
			final InitPackageResourcesParam resparam, XModuleResources moduleResources) {
		
		XSysUiStatusIconResources.prefs = prefs;
		XSysUiStatusIconResources.resparam = resparam;
		XSysUiStatusIconResources.moduleResources = moduleResources;

		if (prefs.getString("signalIcons",
				moduleResources.getString(R.string.att_icons)).equals(
				moduleResources.getString(R.string.minimal_icons))) {
			setMinimalIconSet();
		}

		if (prefs.getBoolean("statusIconColorEnabled", false)) {
			setIconHue();
		}

	}

	private static void setMinimalIconSet() {
		String[] minimalIcons = moduleResources
				.getStringArray(R.array.minimal_sysui_replacements);
		for (String resName : minimalIcons) {
			resparam.res
					.setReplacement(Packages.SYSTEM_UI, "drawable", resName,
							moduleResources.fwd(moduleResources.getIdentifier(
									resName + "_minimal", "drawable",
									Packages.NOTTACH_XPOSED)));
		}
		resparam.res.setReplacement(Packages.SYSTEM_UI, "layout",
				"signal_cluster_view",
				moduleResources.fwd(R.layout.signal_cluster_view_minimal));
	}

	private static void setIconHue() {
		for (final String string : sysUiStatusDrawables) {
			final Drawable replacement = resparam.res.getDrawable(resparam.res
					.getIdentifier(string, "drawable", Packages.SYSTEM_UI));
			replacement.setColorFilter(
					prefs.getInt("statusIconColor", Color.WHITE),
					PorterDuff.Mode.MULTIPLY);
			resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable", string,
					new XResources.DrawableLoader() {

						@Override
						public Drawable newDrawable(XResources res, int id)
								throws Throwable {
							return replacement;
						}
					});
		}
	}

}
