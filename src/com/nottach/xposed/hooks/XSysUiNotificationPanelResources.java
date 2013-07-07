package com.nottach.xposed.hooks;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.CheckBox;

import com.nottach.xposed.R;
import com.nottach.xposed.R.dimen;
import com.nottach.xposed.R.drawable;
import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated.LayoutInflatedParam;

public class XSysUiNotificationPanelResources {

	private static XSharedPreferences prefs;
	private static InitPackageResourcesParam resparam;
	private static XModuleResources moduleResources;

	public static void doHook(XSharedPreferences prefs,
			InitPackageResourcesParam resparam, XModuleResources moduleResources) {

		XSysUiNotificationPanelResources.prefs = prefs;
		XSysUiNotificationPanelResources.resparam = resparam;
		XSysUiNotificationPanelResources.moduleResources = moduleResources;

		if (prefs.getBoolean("notificationHeaderColorEnabled", false)) {
			setHeaderBackground();
			setButtonBackground();
		}

		if (prefs.getBoolean("notificationHeaderButtonDividerColorEnabled",
				false)) {
			setHeaderButtonDividers();
		}

		if (prefs.getBoolean("notificationTitleBarColorEnabled", false)) {
			setTitleBackground();
		}

		if (prefs.getBoolean("notificationNotificationColorEnabled", false)) {
			setNotificationBackground();
		}

		if (prefs.getBoolean("notificationNotificationPressedColorEnabled",
				false)) {
			setNotificationPressedBackground();
		}

		if (prefs.getBoolean("notificationClearColorEnabled", false)) {
			setClearButtonBackground();
		}

		if (prefs.getBoolean("notificationHandleColorEnabled", false)) {
			setCloseHandleBar();
		}

		if (prefs.getBoolean("notificationHandleBackgroundColorEnabled", false)) {
			setCloseHandleBackground();
		}

		if (prefs.getBoolean("notificationRemoveGaps", false)) {
			removeGaps();
		}

		removeDividers();


		if (prefs.getBoolean("autoBrightnessToggleColorEnabled", false)) {
			setBrightnessCheckBoxColor(prefs);
		}

	}

	private static void setBrightnessCheckBoxColor(
			final XSharedPreferences prefs) {
		resparam.res.hookLayout(Packages.SYSTEM_UI, "layout",
				"status_bar_toggle_slider", new XC_LayoutInflated() {

					@Override
					public void handleLayoutInflated(LayoutInflatedParam liparam)
							throws Throwable {
						CheckBox checkBox = (CheckBox) liparam.view
								.findViewById(liparam.res.getIdentifier(
										"toggle", "id", Packages.SYSTEM_UI));
						Drawable drawable = moduleResources
								.getDrawable(R.drawable.tw_btn_check_holo_dark);
						drawable.setColorFilter(
								prefs.getInt("autoBrightnessToggleColor",
										Color.parseColor("#ff33b5e5")),
								PorterDuff.Mode.MULTIPLY);
						checkBox.setButtonDrawable(drawable);
					}
				});
	}

	private static void removeDividers() {
		if (prefs.getBoolean("notificationHideCloseHandleDivider", false)) {
			resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
					"close_handler_divider", new XResources.DrawableLoader() {

						@Override
						public Drawable newDrawable(XResources res, int id)
								throws Throwable {
							return new ColorDrawable(Color.TRANSPARENT);
						}
					});
		}
		if (prefs.getBoolean("notificationHideNotificationDividers", false)) {
			resparam.res.setReplacement(Packages.SYSTEM_UI, "dimen",
					"notification_row_divider_height",
					moduleResources.fwd(R.dimen.zero));
			resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
					"notification_bottom_line",
					new XResources.DrawableLoader() {

						@Override
						public Drawable newDrawable(XResources res, int id)
								throws Throwable {
							return new ColorDrawable(Color.TRANSPARENT);
						}
					});
		}

	}

	private static void setHeaderButtonDividers() {

		resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
				"tw_quick_panel_plnm_setting_dv",
				new XResources.DrawableLoader() {
					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {
						return new ColorDrawable(prefs.getInt(
								"notificationHeaderButtonDividerColor",
								Color.BLACK));
					}
				});
	}

	private static void removeGaps() {
		resparam.res.setReplacement(Packages.SYSTEM_UI, "dimen",
				"quick_setting_button_gap", moduleResources.fwd(R.dimen.zero));
	}

	private static void setButtonBackground() {
		resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
				"ic_notify_button_bg",
				moduleResources.fwd(R.drawable.ic_notify_button_bg));
	}

	private static void setNotificationBackground() {
		resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
				"tw_notification_background_null",
				new XResources.DrawableLoader() {
					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {
						return new ColorDrawable(prefs.getInt(
								"notificationNotificationColor", Color.BLACK));
					}
				});
	}

	private static void setNotificationPressedBackground() {
		resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
				"tw_notification_background_pressed",
				new XResources.DrawableLoader() {
					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {
						return new ColorDrawable(
								XSysUiNotificationPanelResources.prefs.getInt(
										"notificationNotificationPressedColor",
										Color.parseColor("#ff33b5e5")));
					}
				});
	}

	private static void setTitleBackground() {
		resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
				"notification_title_background",
				new XResources.DrawableLoader() {
					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {
						return new ColorDrawable(prefs.getInt(
								"notificationTitleBarColor", Color.DKGRAY));
					}
				});
	}

	private static void setHeaderBackground() {
		resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
				"notification_header_bg", new XResources.DrawableLoader() {
					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {
						return new ColorDrawable(prefs.getInt(
								"notificationHeaderColor", Color.BLACK));
					}
				});
	}

	private static void setClearButtonBackground() {
		resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
				"quick_panel_clearbtn_normal", new XResources.DrawableLoader() {

					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {
						return new ColorDrawable(prefs.getInt(
								"notificationClearColor", Color.TRANSPARENT));
					}
				});
	}

	private static void setCloseHandleBar() {
		resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
				"status_bar_close", new XResources.DrawableLoader() {
					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {

						Drawable[] layers = new Drawable[2];
						if (prefs.getBoolean(
								"notificationHandleBackgroundColorEnabled",
								false)) {
							layers[0] = new ColorDrawable(prefs.getInt(
									"notificationHandleBackgroundColor",
									Color.BLACK));
						} else {
							layers[0] = new ColorDrawable(Color.BLACK);
						}
						Drawable closeOn = moduleResources
								.getDrawable(R.drawable.status_bar_close);
						closeOn.setColorFilter(
								prefs.getInt("notificationHandleColor",
										Color.parseColor("#ff33b5e5")),
								PorterDuff.Mode.MULTIPLY);
						layers[1] = closeOn;

						return new LayerDrawable(layers);
					}
				});
		if (prefs.getBoolean("notificationHandleBackgroundColorEnabled", false)) {
			resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
					"close_handler_divider", new XResources.DrawableLoader() {
						@Override
						public Drawable newDrawable(XResources res, int id)
								throws Throwable {
							return new ColorDrawable(prefs.getInt(
									"notificationHandleBackgroundColor",
									Color.BLACK));
						}
					});
		}

	}

	private static void setCloseHandleBackground() {
		resparam.res.setReplacement(Packages.SYSTEM_UI, "drawable",
				"status_bar_bg_tile", new XResources.DrawableLoader() {
					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {
						return new ColorDrawable(prefs.getInt(
								"notificationHandleBackgroundColor",
								Color.BLACK));
					}
				});
	}

}
