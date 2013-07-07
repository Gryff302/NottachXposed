package com.nottach.xposed.hooks;

import java.lang.reflect.Method;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XSysUiNotificationPanelPackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XSysUiNotificationPanelPackage.prefs = prefs;
		XSysUiNotificationPanelPackage.classLoader = classLoader;

		handleLoadNotificationShade();

		handleUpdateTextResources();

		handleMakeStatusBarView();

		if (prefs.getBoolean("notificationPanelBackgroundEnabled", false)) {
			setPanelBackgroundImage();
		}
	}

	private static void setPanelBackgroundImage() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.phone.NotificationPanelView", classLoader,
				"onFinishInflate", new XC_MethodHook() {
					@SuppressWarnings("deprecation")
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						FrameLayout panelView = (FrameLayout) param.thisObject;

						if (prefs.getBoolean(
								"notificationPanelBackgroundIsColor", true)) {
							panelView.setBackgroundDrawable(new ColorDrawable(
									prefs.getInt("notificationPanelBackground",
											Color.BLACK)));
						} else {
							String uriString = prefs.getString(
									"notificationPanelBackgroundImageUri", "");
							if (uriString == null || uriString.equals("")) {
								XposedBridge.log("Panel Image Uri Is null");
								return;
							}
							Uri uri = Uri.parse(uriString);
							Bitmap bitmap = MediaStore.Images.Media
									.getBitmap(panelView.getContext()
											.getContentResolver(), uri);

							panelView.setBackgroundDrawable(new BitmapDrawable(
									bitmap));
						}
					}
				});

	}

	private static void handleMakeStatusBarView() {
		Class<?> claZ = XposedHelpers.findClass(Packages.SYSTEM_UI
				+ ".statusbar.phone.PhoneStatusBar", classLoader);
		Method MetZ = XposedHelpers.findMethodExact(claZ, "makeStatusBarView");
		XposedBridge.hookMethod(MetZ, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param)
					throws Throwable {
				TextView tvCarrier = (TextView) XposedHelpers.getObjectField(
						param.thisObject, "mCarrierLabel");
				if (prefs.getBoolean(
						"notificationHandleCarrierTextColorEnabled", false)) {
					tvCarrier.setTextColor(prefs.getInt(
							"notificationHandleCarrierTextColor",
							Color.parseColor("#ff33b5e5")));
				}

			}
		});
	}

	private static void handleLoadNotificationShade() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.phone.PhoneStatusBar", classLoader,
				"loadNotificationShade", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {

						if (prefs.getBoolean("hideNoNotificationsTitleBar",
								false)) {
							((LinearLayout) XposedHelpers.getObjectField(
									param.thisObject, "mNoNotificationsTitle"))
									.setVisibility(View.GONE);
						}
						if (prefs.getBoolean(
								"notificationHeaderButtonColorEnabled", false)) {
							String[] buttons = new String[] {
									"mSettingsButton", "mBasicSettingsButton",
									"mNotificationButton",
									"mSettingsEditButton", };
							final int iconColor = prefs.getInt(
									"notificationHeaderButtonColor",
									Color.parseColor("#ff33b5e5"));
							for (final String string : buttons) {
								ImageView settingIV = (ImageView) XposedHelpers
										.getObjectField(param.thisObject,
												string);
								settingIV.setColorFilter(iconColor,
										PorterDuff.Mode.MULTIPLY);
							}
						}
					}
				});

	}

	private static void handleUpdateTextResources() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.phone.PhoneStatusBar", classLoader,
				"updateTextResources", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						if (prefs.getBoolean(
								"notificationTitleBarTextColorEnabled", false)) {
							String[] titleTexts = new String[] {
									"mOngoingNotificationText",
									"mLatestNotificationText",
									"mNoNotificationText", "mClearButton" };
							int titleColor = prefs.getInt(
									"notificationTitleBarTextColor",
									Color.WHITE);
							for (final String titleText : titleTexts) {
								TextView textView = (TextView) XposedHelpers
										.getObjectField(param.thisObject,
												titleText);
								textView.setTextColor(titleColor);
							}
						}
					}
				});
	}

}
