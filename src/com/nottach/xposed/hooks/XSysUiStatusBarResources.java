package com.nottach.xposed.hooks;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.nottach.xposed.R;
import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class XSysUiStatusBarResources {

	private static XSharedPreferences prefs;
	private static InitPackageResourcesParam resparam;
	private static XModuleResources moduleResources;

	public static void doHook(XSharedPreferences prefs,
			InitPackageResourcesParam resparam, XModuleResources moduleResources) {

		XSysUiStatusBarResources.prefs = prefs;
		XSysUiStatusBarResources.resparam = resparam;
		XSysUiStatusBarResources.moduleResources = moduleResources;

		if (prefs.getBoolean("statusbarColorEnabled", false)) {
			setBackgroundDrawable();
		}

		if (!prefs.getString("clockPosition",
				moduleResources.getString(R.string.right)).equals(
				moduleResources.getString(R.string.right))) {
			setClockLayout();
		}
	}

	private static void setBackgroundDrawable() {
		final int statusbarColor = prefs.getInt("statusbarColor",
				Color.TRANSPARENT);
		resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
				"status_bar_background", new XResources.DrawableLoader() {
					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {
						return new ColorDrawable(statusbarColor);
					}
				});
	}

	private static void setClockLayout() {
		if (prefs.getString("clockPosition",
				moduleResources.getString(R.string.right)).equals(
				moduleResources.getString(R.string.center))) {
			resparam.res.setReplacement(Packages.SYSTEM_UI, "layout",
					"status_bar",
					moduleResources.fwd(R.layout.status_bar_center));
		} else if (prefs.getString("clockPosition",
				moduleResources.getString(R.string.right)).equals(
				moduleResources.getString(R.string.left))) {
			resparam.res
					.setReplacement(Packages.SYSTEM_UI, "layout", "status_bar",
							moduleResources.fwd(R.layout.status_bar_left));
		}
	}

}
