package com.nottach.xposed.hooks;

import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XSecPhonePackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {
		XSecPhonePackage.prefs = prefs;
		XSecPhonePackage.classLoader = classLoader;

		if (prefs.getBoolean("enableCallRecording", false)) {
			enableCallRecording();
			enableIncomingCallRecordingPopUp();
		}

		if (prefs.getBoolean("disableEscalatingRingtone", false)) {
			disableEscalatingRing();
		}

	}

	private static void disableEscalatingRing() {
		XposedHelpers.findAndHookMethod(Packages.PHONE + ".Ringer",
				classLoader, "ring", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						XposedHelpers.setBooleanField(param.thisObject,
								"mIsRingEscalating", false);
					}
				});
	}

	private static void enableIncomingCallRecordingPopUp() {

		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = String.class;
		arrayOfObject[1] = new XC_MethodHook() {
			protected void beforeHookedMethod(
					XC_MethodHook.MethodHookParam param) throws Throwable {
				if ("enbale_voicerecording_popup_when_incoming"
						.equals(param.args[0])) {
					param.setResult(Boolean.TRUE);
				}
			}
		};
		XposedHelpers.findAndHookMethod("com.android.phone.PhoneFeature",
				classLoader, "hasFeature", arrayOfObject);

	}

	private static void enableCallRecording() {

		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = String.class;
		arrayOfObject[1] = new XC_MethodHook() {
			protected void beforeHookedMethod(
					XC_MethodHook.MethodHookParam param) throws Throwable {
				if ("voice_call_recording".equals(param.args[0])) {
					param.setResult(Boolean.TRUE);
				}
			}
		};
		XposedHelpers.findAndHookMethod("com.android.phone.PhoneFeature",
				classLoader, "hasFeature", arrayOfObject);

	}

}
