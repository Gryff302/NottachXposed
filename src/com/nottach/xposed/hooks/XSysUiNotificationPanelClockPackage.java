package com.nottach.xposed.hooks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import com.nottach.xposed.receivers.NottachLaunchReceiver;
import com.nottach.xposed.utils.Packages;
import com.nottach.xposed.utils.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XSysUiNotificationPanelClockPackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(final XSharedPreferences prefs,
			ClassLoader classLoader) {

		XSysUiNotificationPanelClockPackage.prefs = prefs;
		XSysUiNotificationPanelClockPackage.classLoader = classLoader;

		setClockActivity();

		setClockLongActivity();

		handleUpdateClock();

	}

	private static void setClockActivity() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.phone.PhoneStatusBar$25", classLoader, "onClick",
				View.class, new XC_MethodReplacement() {
					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						View view = (View) param.args[0];
						Context context = view.getContext();
						Intent intent = new Intent();
						intent.setAction(NottachLaunchReceiver.START_CLOCK_ACTIVITY);
						context.sendBroadcast(intent);
						Utils.closeStatusBar(context);
						return true;
					}
				});

	}

	private static void setClockLongActivity() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.phone.PhoneStatusBar", classLoader,
				"makeStatusBarView", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						View view = (View) XposedHelpers.getObjectField(
								param.thisObject, "mDateTimeView");
						view.setOnLongClickListener(new OnLongClickListener() {

							@Override
							public boolean onLongClick(View v) {
								Context context = v.getContext();
								Intent intent = new Intent();
								intent.setAction(NottachLaunchReceiver.START_CLOCK_LONG_ACTIVITY);
								context.sendBroadcast(intent);
								try {
									Utils.closeStatusBar(context);
								} catch (Throwable e) {
									e.printStackTrace();
								}
								return true;
							}
						});
					}
				});
	}

	private static void handleUpdateClock() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.policy.DateView", classLoader, "updateClock",
				new XC_MethodHook() {

					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						if (prefs.getBoolean("clockDateTwoLineFormat", false)) {
							XposedHelpers.setObjectField(param.thisObject,
									"mDateFormat", new String(
											"EEEE\nMMMM d, yyyy"));
						}

					}

					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						TextView tvDate = (TextView) param.thisObject;

						if (prefs.getBoolean("clockDateColorEnabled", false)) {
							int color = prefs.getInt("clockDateColor",
									Color.WHITE);
							tvDate.setTextColor(color);
						}

						tvDate.setSingleLine(false);
						tvDate = Utils.setTypeface(prefs, tvDate);

					}
				});
	}

}
