package com.nottach.xposed.hooks;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.CheckBox;

import com.nottach.xposed.R;
import com.nottach.xposed.R.array;
import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class XSysUiQuickSettingsResources {

	static InitPackageResourcesParam resparam;
	static XModuleResources moduleResources;
	private static int quickSettingsTiles = 5;

	public static void doHook(final XSharedPreferences prefs,
			InitPackageResourcesParam resparam, XModuleResources moduleResources) {

		quickSettingsTiles = prefs.getInt("quickSettingsColumns", 5);
		resparam.res.setReplacement(Packages.SYSTEM_UI, "integer",
				"quick_settings_num_columns", quickSettingsTiles);

		if (prefs.getBoolean("quickSettingTileColorEnabled", false)) {
			resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
					"tw_quick_panel_quick_setting_button_bg_normal",
					new XResources.DrawableLoader() {

						@Override
						public Drawable newDrawable(XResources res, int id)
								throws Throwable {
							return new ColorDrawable(prefs.getInt(
									"quickSettingTileColor", Color.BLACK));
						}
					});
		}

		if (prefs.getBoolean("quickSettingTilePressedColorEnabled", false)) {
			resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
					"tw_quick_panel_quick_setting_button_bg_pressed",
					new XResources.DrawableLoader() {

						@Override
						public Drawable newDrawable(XResources res, int id)
								throws Throwable {
							return new ColorDrawable(prefs.getInt(
									"quickSettingTilePressedColor",
									Color.parseColor("#ff33b5e5")));
						}
					});
		}

		if (prefs.getBoolean("quickSettingIconColorEnabled", false)) {
			XSysUiQuickSettingsResources.resparam = resparam;
			XSysUiQuickSettingsResources.moduleResources = moduleResources;
			String[] resNames = moduleResources
					.getStringArray(R.array.toggle_drawable_res_names);
			for (String string : resNames) {
				replaceToggleIcon(string);
			}
		}
		
	}

	private static void replaceToggleIcon(final String resName) {
		resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable", resName,
				new XResources.DrawableLoader() {
					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {
						return moduleResources.getDrawable(moduleResources
								.getIdentifier(resName, "drawable",
										Packages.NOTTACH_XPOSED));
					}
				});
	}

}
