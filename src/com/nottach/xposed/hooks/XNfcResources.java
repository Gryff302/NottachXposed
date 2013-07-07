package com.nottach.xposed.hooks;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.nottach.xposed.R;
import com.nottach.xposed.R.drawable;
import com.nottach.xposed.R.string;
import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class XNfcResources {

	private static XSharedPreferences prefs;
	private static InitPackageResourcesParam resparam;
	private static XModuleResources moduleResources;
	private static Drawable replacementNfcDrawable;

	public static void doHook(final XSharedPreferences prefs,
			final InitPackageResourcesParam resparam,
			XModuleResources moduleResources) {

		XNfcResources.prefs = prefs;
		XNfcResources.resparam = resparam;
		XNfcResources.moduleResources = moduleResources;

		if (!prefs.getString("nfcIcon",
				moduleResources.getString(R.string.att_icon)).equals(
				moduleResources.getString(R.string.att_icon))) {
			setIcon();
		}

	}

	private static void setIcon() {
		if (prefs.getString("nfcIcon",
				moduleResources.getString(R.string.att_icon)).equals(
				moduleResources.getString(R.string.global_icon))) {
			replacementNfcDrawable = resparam.res
					.getDrawable(resparam.res.getIdentifier("stat_sys_nfc_on",
							"drawable", Packages.NFC));
		} else if (prefs.getString("nfcIcon",
				moduleResources.getString(R.string.att_icon)).equals(
				moduleResources.getString(R.string.minimal_icon))) {
			replacementNfcDrawable = moduleResources
					.getDrawable(R.drawable.stat_sys_nfc_on_minimal);
		}
		if (prefs.getBoolean("signalIconColorEnabled", false)) {
			replacementNfcDrawable = getSignalColoredDrawable(prefs,
					replacementNfcDrawable);
		}
		resparam.res.setReplacement(Packages.NFC, "drawable",
				"stat_sys_nfc_on_att", new XResources.DrawableLoader() {

					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {
						return replacementNfcDrawable;
					}
				});
	}

	private static Drawable getSignalColoredDrawable(XSharedPreferences prefs,
			Drawable drawable) {
		drawable.setColorFilter(
				prefs.getInt("signalIconColor", Color.parseColor("#ff33b5e5")),
				PorterDuff.Mode.MULTIPLY);
		return drawable;
	}
}
