package com.nottach.xposed.hooks;

import android.graphics.Typeface;
import android.widget.TextView;

import com.nottach.xposed.utils.Packages;
import com.nottach.xposed.utils.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XSysUiStatusBarCarrierPackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XSysUiStatusBarCarrierPackage.prefs = prefs;
		XSysUiStatusBarCarrierPackage.classLoader = classLoader;

		setTypefaceSizeAndColor();

		setTextAndVisibility();
	}

	private static void setTextAndVisibility() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.policy.NetworkController", classLoader,
				"updateNetworkName", boolean.class, String.class,
				boolean.class, String.class, new XC_MethodHook() {

					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						if (prefs.getBoolean("hideCarrierLabel", false)) {
							param.args[3] = "".toString();
							return;
						}
						String cusCarrier = prefs.getString(
								"customCarrierLabel", "");
						if (!cusCarrier.equals("")) {
							param.args[3] = cusCarrier;
						}
					}
				});
	}

	private static void setTypefaceSizeAndColor() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.policy.NetworkController", classLoader,
				"addATTMobileLabelView", TextView.class, new XC_MethodHook() {

					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						TextView textView = (TextView) param.args[0];
						int textSize = 18;
						String tsPrefVal = prefs.getString("carrierSize",
								"Medium");
						if (tsPrefVal.equals("Small")) {
							textSize = 16;
						} else if (tsPrefVal.equals("Large")) {
							textSize = 20;
						}
						textView.setTextSize(textSize);
						textView = Utils.setTypeface(prefs, textView);
						if (prefs.getBoolean("carrierLabelColorEnabled", false)) {
							textView.setTextColor(prefs.getInt(
									"carrierLabelColor", android.R.color.white));
						}
					}
				});
	}

}
