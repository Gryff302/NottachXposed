package com.nottach.xposed.hooks;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.ImageView;
import android.widget.TextView;

import com.nottach.xposed.utils.Packages;
import com.nottach.xposed.utils.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XSysUiBatteryPackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XSysUiBatteryPackage.prefs = prefs;
		XSysUiBatteryPackage.classLoader = classLoader;

		if (prefs.getBoolean("hideFullBatteryNotification", false)) {
			hideFullBatteryNotification();
		}

		setBatteryTextColorStyleAndSize();

		if (!prefs.getString("batteryTextSuffix", "").equals("")) {
			setBatteryTextSuffix();
		}

		if (prefs.getBoolean("hideBatteryIcon", false)) {
			hideBatteryIcon();
		} else if (prefs.getBoolean("circleBatteryColorEnabled", false)) {
			setBatteryIconColor();
		}
	}

	private static void setBatteryIconColor() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.policy.BatteryController", classLoader,
				"addIconView", ImageView.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						((ImageView) param.args[0]).setColorFilter(
								prefs.getInt("circleBatteryColor", Color.WHITE),
								PorterDuff.Mode.MULTIPLY);
					}

				});
	}

	private static void hideBatteryIcon() {
		XposedHelpers
				.findAndHookMethod(Packages.SYSTEM_UI
						+ ".statusbar.policy.BatteryController", classLoader,
						"addIconView", ImageView.class,
						XC_MethodReplacement.DO_NOTHING);
	}

	private static void setBatteryTextSuffix() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.policy.BatteryController", classLoader,
				"onReceive", Context.class, Intent.class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						@SuppressWarnings("unchecked")
						ArrayList<TextView> mLabelViews = (ArrayList<TextView>) XposedHelpers
								.getObjectField(param.thisObject, "mLabelViews");
						for (TextView textView : mLabelViews) {
							String text = textView
									.getText()
									.toString()
									.replace(
											"%",
											prefs.getString(
													"batteryTextSuffix", ""));
							textView.setText(text);
						}

					}
				});

	}

	private static void setBatteryTextColorStyleAndSize() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.policy.BatteryController", classLoader,
				"addLabelView", TextView.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						TextView textView = (TextView) param.args[0];
						if (prefs.getBoolean("batteryTextColorEnabled", false)) {
							textView.setTextColor(prefs.getInt(
									"batteryTextColor", Color.WHITE));
						}
						int textSize = 18;
						String tsPrefVal = prefs.getString("batterySize",
								"Medium");
						if (tsPrefVal.equals("Small")) {
							textSize = 16;
						} else if (tsPrefVal.equals("Large")) {
							textSize = 20;
						}
						textView.setTextSize(textSize);
						textView = Utils.setTypeface(prefs, textView);
					}
				});
	}

	private static void hideFullBatteryNotification() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI + ".power.PowerUI",
				classLoader, "notifyFullBatteryNotification",
				XC_MethodReplacement.DO_NOTHING);
	}

}
