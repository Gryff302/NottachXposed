package com.nottach.xposed.hooks;

import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.nottach.xposed.utils.Packages;
import com.nottach.xposed.utils.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XSysUiStatusBarClockPackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {
		XSysUiStatusBarClockPackage.prefs = prefs;
		XSysUiStatusBarClockPackage.classLoader = classLoader;

		setClockVisibilityColorAndSize();

	}

	private static void setClockVisibilityColorAndSize() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.policy.Clock", classLoader, "updateClock",
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						Context context = ((View) param.thisObject)
								.getContext();

						if (Settings.System.getString(
								context.getContentResolver(),
								Settings.System.NEXT_ALARM_FORMATTED) != null) {
							String nextAlarm = Settings.System.getString(
									context.getContentResolver(),
									Settings.System.NEXT_ALARM_FORMATTED);
							if (nextAlarm.length() != 0) {
								Class<?> classPhoneStatusBarPolicy = XposedHelpers
										.findClass(
												Packages.SYSTEM_UI
														+ ".statusbar.phone.PhoneStatusBarPolicy",
												classLoader);
								Object phoneStatusBarPolicy = XposedHelpers
										.newInstance(classPhoneStatusBarPolicy,
												((View) param.thisObject)
														.getContext());
								XposedHelpers.callMethod(phoneStatusBarPolicy,
										"updateAlarm",
										new Intent().putExtra("alarmSet", true));
							}
						}

						TextView tvClock = (TextView) param.thisObject;
						tvClock = Utils.setTypeface(prefs, tvClock);

						if (!XposedHelpers.getBooleanField(param.thisObject,
								"mExpandedHeader")) {

							String text = tvClock.getText().toString();

							String style = prefs
									.getString("amPmStyle", "Large");
							if (style.equals("Small")) {
								text = text.replace(" AM", " am");
								text = text.replace(" PM", " pm");
							} else if (style.equals("Hide")) {
								text = text.replace(" AM", "");
								text = text.replace(" PM", "");
							}
							if (prefs.getBoolean("showClockDate", false)) {
								Calendar calendar = Calendar.getInstance();
								calendar.setTimeInMillis(System
										.currentTimeMillis());

								text = calendar.getDisplayName(Calendar.MONTH,
										Calendar.SHORT, Locale.getDefault())
										+ " "
										+ calendar.get(Calendar.DAY_OF_MONTH)
										+ ", " + text;
							}
							tvClock.setText(text);
							int textSize = 18;
							String tsPrefVal = prefs.getString("clockSize",
									"Medium");
							if (tsPrefVal.equals("Small")) {
								textSize = 16;
							} else if (tsPrefVal.equals("Large")) {
								textSize = 20;
							}
							tvClock.setTextSize(textSize);

							if (prefs.getBoolean("clockColorEnabled", false)) {
								tvClock.setTextColor(prefs.getInt("clockColor",
										Color.WHITE));
							}

							if (prefs.getString("clockPosition", "Right")
									.equals("Hide")) {
								tvClock.setVisibility(View.GONE);
							}

						} else {

							if (prefs
									.getBoolean("clockDateColorEnabled", false)) {
								int color = prefs.getInt("clockDateColor",
										Color.WHITE);
								tvClock.setTextColor(color);
							}

						}

					}
				});

	}

}
