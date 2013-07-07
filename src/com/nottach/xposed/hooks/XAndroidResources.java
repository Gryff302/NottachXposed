package com.nottach.xposed.hooks;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.nottach.xposed.R;
import com.nottach.xposed.R.array;
import com.nottach.xposed.R.string;
import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class XAndroidResources {

	static final String[] androidStatusDrawables = new String[] {
			"stat_sys_data_bluetooth", "stat_notify_chat", "stat_notify_error",
			"stat_notify_missed_call", "stat_sys_wifi_enabled",
			"stat_notify_email_generic", "stat_notify_sdcard",
			"stat_notify_sdcard_prepare", "stat_sys_gps_on",
			"stat_sys_headset", "stat_sys_phone_call_on_hold", "stat_ecb_mode",
			"stat_notify_disabled", "stat_sys_phone_call_forward",
			"stat_sys_upload_anim0", "stat_sys_upload_anim3", "stat_sys_voice",
			"stat_sys_wifi_not_connected", "stat_sys_upload_anim2",
			"stat_sys_wifi_p2p_connected", "stat_sys_download_anim0",
			"stat_notify_call_mute", "stat_notify_car_mode",
			"stat_notify_voicemail", "stat_notify_wifi_in_range",
			"stat_sys_download_anim5", "stat_sys_secure",
			"stat_notify_sim_toolkit", "stat_sys_data_usb", "stat_sys_adb",
			"stat_sys_download_anim2", "stat_sys_download_anim4",
			"stat_sys_upload_anim5", "stat_sys_wifi_no_network",
			"stat_sys_throttled", "stat_sys_upload_anim1",
			"stat_notify_disk_full", "stat_notify_gmail",
			"stat_sys_download_anim1", "stat_sys_wifi_connected",
			"stat_sys_wifi_disabled", "stat_notify_more",
			"stat_sys_download_anim3", "stat_sys_upload_anim4",
			"stat_sys_warning", "stat_notify_sdcard_usb",
			"settings_facedetection_dim", "settings_facedetection_nor",
			"stat_sys_speakerphone" };

	private static XSharedPreferences prefs;
	private static InitPackageResourcesParam resparam;
	private static XModuleResources moduleResources;

	public static void doHook(XSharedPreferences prefs,
			InitPackageResourcesParam resparam, XModuleResources moduleResources) {

		XAndroidResources.prefs = prefs;
		XAndroidResources.resparam = resparam;
		XAndroidResources.moduleResources = moduleResources;

		if (prefs.getString("signalIcons",
				moduleResources.getString(R.string.att_icons)).equals(
				moduleResources.getString(R.string.minimal_icons))) {
			setMinimalIconSet();
		}

		if (prefs.getBoolean("statusIconColorEnabled", false)) {
			setAndroidStatusIconResources();
		}


	}

	private static void setMinimalIconSet() {
		String[] minimalIcons = moduleResources
				.getStringArray(R.array.minimal_framework_replacements);
		for (String resName : minimalIcons) {
			resparam.res
					.setReplacement(Packages.ANDROID, "drawable", resName,
							moduleResources.fwd(moduleResources.getIdentifier(
									resName + "_minimal", "drawable",
									Packages.NOTTACH_XPOSED)));
		}
	}

	private static void setAndroidStatusIconResources() {
		for (final String string : androidStatusDrawables) {
			final Drawable replacement = resparam.res.getDrawable(resparam.res
					.getIdentifier(string, "drawable", Packages.ANDROID));
			replacement.setColorFilter(
					prefs.getInt("statusIconColor", Color.WHITE),
					PorterDuff.Mode.MULTIPLY);
			resparam.res.setReplacement(Packages.ANDROID, "drawable", string,
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
