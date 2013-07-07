package com.nottach.xposed.hooks;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class XSecVoiceResources {

	private static XSharedPreferences prefs;

	public static void doHook(XSharedPreferences prefs,
			InitPackageResourcesParam resparam, XModuleResources moduleResources) {

		XSecVoiceResources.prefs = prefs;

		if (prefs.getBoolean("statusIconColorEnabled", false)) {
			setStatusIconResources(resparam);
		}
	}

	private static void setStatusIconResources(
			final InitPackageResourcesParam resparam) {
		resparam.res.setReplacement(Packages.S_VOICE, "drawable",
				"stat_notify_car_mode", new XResources.DrawableLoader() {

					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {
						Drawable replacement = resparam.res
								.getDrawable(resparam.res.getIdentifier(
										"stat_notify_car_mode", "drawable",
										Packages.S_VOICE));
						replacement.setColorFilter(
								prefs.getInt("statusIconColor", Color.WHITE),
								PorterDuff.Mode.MULTIPLY);
						return replacement;
					}
				});
	}

}
