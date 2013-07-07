package com.nottach.xposed.hooks;

import java.lang.reflect.Method;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.ImageView;

import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XSysUiSignalPackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XSysUiSignalPackage.prefs = prefs;
		XSysUiSignalPackage.classLoader = classLoader;

		if (prefs.getString("signalIcons", "AT&#38;T icon set").equals(
				"Global icon set")) {
			setGlobalSignalIcons();
		}

		setNumberOfSignalBars();

		if (prefs.getBoolean("signalIconColorEnabled", false)) {
			setSysUiSignalIconHue();
		}
	}

	private static void setGlobalSignalIcons() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.Feature", classLoader, "getOperator",
				new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return "OPEN".toString();
					}
				});
	}

	private static void setNumberOfSignalBars() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.Feature", classLoader,
				"getMaxLevelOfSignalStrengthIndicator",
				new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return (int) prefs.getInt("numSignalBars", 5);
					}
				});
	}

	private static void setSysUiSignalIconHue() {
		Method signalClusterView = XposedHelpers.findMethodExact(
				XposedHelpers.findClass(Packages.SYSTEM_UI
						+ ".statusbar.SignalClusterView", classLoader),
				"onAttachedToWindow");

		XposedBridge.hookMethod(signalClusterView, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param)
					throws Throwable {
				int iconColor = prefs.getInt("signalIconColor",
						Color.parseColor("#ff33b5e5"));
				((ImageView) XposedHelpers.getObjectField(param.thisObject,
						"mMobile")).setColorFilter(iconColor,
						PorterDuff.Mode.MULTIPLY);
				((ImageView) XposedHelpers.getObjectField(param.thisObject,
						"mMobileActivity")).setColorFilter(iconColor,
						PorterDuff.Mode.MULTIPLY);
				((ImageView) XposedHelpers.getObjectField(param.thisObject,
						"mMobileType")).setColorFilter(iconColor,
						PorterDuff.Mode.MULTIPLY);
				((ImageView) XposedHelpers.getObjectField(param.thisObject,
						"mMobileRoaming")).setColorFilter(iconColor,
						PorterDuff.Mode.MULTIPLY);
				((ImageView) XposedHelpers.getObjectField(param.thisObject,
						"mWifi")).setColorFilter(iconColor,
						PorterDuff.Mode.MULTIPLY);
				((ImageView) XposedHelpers.getObjectField(param.thisObject,
						"mWifiActivity")).setColorFilter(iconColor,
						PorterDuff.Mode.MULTIPLY);
				((ImageView) XposedHelpers.getObjectField(param.thisObject,
						"mEthernet")).setColorFilter(iconColor,
						PorterDuff.Mode.MULTIPLY);
				((ImageView) XposedHelpers.getObjectField(param.thisObject,
						"mEthernetActivity")).setColorFilter(iconColor,
						PorterDuff.Mode.MULTIPLY);
				((ImageView) XposedHelpers.getObjectField(param.thisObject,
						"mAirplane")).setColorFilter(iconColor,
						PorterDuff.Mode.MULTIPLY);

			}
		});

	}

}
