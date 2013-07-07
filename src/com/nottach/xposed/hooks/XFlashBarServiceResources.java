package com.nottach.xposed.hooks;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.drawable.Drawable;

import com.nottach.xposed.R;
import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class XFlashBarServiceResources {

	private static InitPackageResourcesParam resparam;
	private static XModuleResources moduleResources;

	public static void doHook(XSharedPreferences prefs,
			InitPackageResourcesParam resparam, XModuleResources moduleResources) {

		XFlashBarServiceResources.resparam = resparam;
		XFlashBarServiceResources.moduleResources = moduleResources;

		if (prefs.getBoolean("enableMwDarkTheme", false)) {
			replaceFlashBarServiceDrawables();
			replaceFlashBarServiceXmls();
		}

	}

	private static void replaceFlashBarServiceXmls() {
		String[] resNames = new String[] { "applist_item" };
		for (final String resName : resNames) {
			resparam.res.setReplacement(Packages.FLASH_BAR_SERVICE, "layout",
					resName, moduleResources.fwd(R.layout.applist_item));
		}
	}

	private static void replaceFlashBarServiceDrawables() {
		String[] resNames = new String[] { "multiwindow_edit_bg",
				"multiwindow_edit_bg_h", "multiwindow_split_control_press",
				"multiwindow_tray_bottom_bg",
				"multiwindow_split_control_ver_press_fixed",
				"multiwindow_split_control_ver_press",
				"multiwindow_split_control_press_fixed",
				"multiwindow_tray_bottom_handle_closed_press",
				"multiwindow_tray_bottom_handle_closed",
				"multiwindow_tray_bottom_handle",
				"multiwindow_tray_bottom_bg_h",
				"multiwindow_tray_bottom_handle_press",
				"multiwindow_tray_left_handle", "multiwindow_tray_left_bg_h",
				"multiwindow_tray_left_bg",
				"multiwindow_tray_fullscreen_press",
				"multiwindow_tray_edit_btn_pressed",
				"multiwindow_tray_close_press",
				"multiwindow_tray_top_handle_press",
				"multiwindow_tray_top_handle_closed_press",
				"multiwindow_tray_top_handle_closed",
				"multiwindow_tray_top_handle", "multiwindow_tray_top_bg_h",
				"multiwindow_tray_top_bg",
				"multiwindow_tray_switch_window_press",
				"multiwindow_tray_right_handle_press",
				"multiwindow_tray_right_handle_closed_press",
				"multiwindow_tray_right_handle_closed",
				"multiwindow_tray_right_handle", "multiwindow_tray_right_bg_h",
				"multiwindow_tray_right_bg",
				"multiwindow_tray_left_handle_press",
				"multiwindow_tray_left_handle_closed_press",
				"multiwindow_tray_left_handle_closed",
				"tw_buttonbarbutton_selector_pressed_holo_dark" };
		for (final String resName : resNames) {
			resparam.res.setReplacement(Packages.FLASH_BAR_SERVICE, "drawable",
					resName, new XResources.DrawableLoader() {

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

}
