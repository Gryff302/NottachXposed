package com.nottach.xposed.hooks;

import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class XMmsResources {

	private static XSharedPreferences prefs;
	private static InitPackageResourcesParam resparam;

	public static void doHook(XSharedPreferences prefs,
			InitPackageResourcesParam resparam) {

		XMmsResources.prefs = prefs;
		XMmsResources.resparam = resparam;

		if (prefs.getBoolean("statusIconColorEnabled", false)) {
			setStatusIconResources();
		}
	}

	private static void setStatusIconResources() {
		resparam.res.setReplacement(Packages.MMS, "drawable",
				"stat_notify_message", new XResources.DrawableLoader() {

					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {
						Drawable replacement = resparam.res
								.getDrawable(resparam.res.getIdentifier(
										"stat_notify_message", "drawable",
										Packages.MMS));
						replacement.setColorFilter(
								prefs.getInt("statusIconColor", Color.WHITE),
								PorterDuff.Mode.MULTIPLY);
						return replacement;
					}
				});
	}

}
