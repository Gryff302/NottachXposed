package com.nottach.xposed.hooks;

import android.preference.Preference;
import android.preference.PreferenceGroup;

import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XMmsPackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XMmsPackage.prefs = prefs;
		XMmsPackage.classLoader = classLoader;

		if (prefs.getBoolean("raiseRecipientLimit", false)) {
			setMaxRecipientLength();
		}

		if (prefs.getInt("raiseMmsMaxSizeLimit", 0) != 0) {
			setMaxMessageSize();
		}

		if (prefs.getInt("raiseMmsImageSizeLimit", 0) != 0) {
			setImageResolution();
		}

		if (prefs.getBoolean("raiseMmsRecipientLimit", false)) {
			raiseMmsRecipientLimit();
		}

		if (prefs.getBoolean("raiseSmsMmsConversion", false)) {
			raiseSmsMmsConversion();
		}

		if (prefs.getBoolean("showScreenOnToggle", false)) {
			enableScreenOnPreference();
		}

		if (prefs.getBoolean("enableSmsSaveRestore", false)) {
			enableSaveRestoreFunction();
		}

		if (prefs.getBoolean("enableSplitMode", false)) {
			enableSplitMode();
		}
	}

	private static void enableSplitMode() {
		XposedHelpers.findAndHookMethod(Packages.MMS + ".MmsConfig",
				classLoader, "getEnableSplitMode", new XC_MethodReplacement() {
					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return true;
					}
				});
	}

	private static void enableSaveRestoreFunction() {
		XposedHelpers.findAndHookMethod(Packages.MMS + ".MmsConfig",
				classLoader, "getEnableSaveRestoreSDCardMessage",
				new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return true;
					}
				});
	}

	private static void enableScreenOnPreference() {
		XposedHelpers.findAndHookMethod(Packages.MMS
				+ ".ui.MessagingPreferenceActivity", classLoader,
				"removePreference", PreferenceGroup.class, Preference.class,
				new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						Preference preference = (Preference) param.args[1];
						if (preference.getKey().equals("pref_key_backlight")) {
							param.args[1] = null;
						}
					}
				});
	}

	private static void raiseSmsMmsConversion() {
		XposedHelpers.findAndHookMethod(Packages.MMS + ".MmsConfig",
				classLoader, "getSmsToMmsTextThreshold",
				new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return (int) 999;
					}
				});
	}

	private static void raiseMmsRecipientLimit() {
		XposedHelpers.findAndHookMethod(Packages.MMS + ".MmsConfig",
				classLoader, "getMmsMaxRecipient", new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return (int) 999;
					}
				});
	}

	private static void setImageResolution() {
		XposedHelpers.findAndHookMethod(Packages.MMS + ".MmsConfig",
				classLoader, "setImageResolution", String.class,
				new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						String s = "HD1080";
						switch (prefs.getInt("raiseMmsImageSizeLimit", 0)) {
						case 1:
							s = "QXGA";
							break;
						case 2:
							s = "WQXGA";
							break;

						default:
							break;
						}
						param.args[0] = s;
					}
				});
	}

	private static void setMaxMessageSize() {
		XposedHelpers.findAndHookMethod(Packages.MMS + ".MmsConfig",
				classLoader, "setMaxMessageSize", int.class,
				new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						int i = 0xfb00;
						switch (prefs.getInt("raiseMmsImageSizeLimit", 0)) {
						case 1:
							i = 0x1f4000;
							break;
						case 2:
							i = 0x3e8000;
							break;

						default:
							break;
						}
						param.args[0] = i;
					}
				});
	}

	private static void setMaxRecipientLength() {
		XposedHelpers.findAndHookMethod(Packages.MMS + ".MmsConfig",
				classLoader, "getMaxRecipientLength",
				new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return (int) 999;
					}
				});
	}

}
