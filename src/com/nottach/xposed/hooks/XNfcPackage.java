package com.nottach.xposed.hooks;

import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XNfcPackage {

	private static final int SCREEN_STATE_ON_LOCKED = 2;
	private static final int SCREEN_STATE_ON_UNLOCKED = 3;
	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;
	private static int behavior;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XNfcPackage.prefs = prefs;
		XNfcPackage.classLoader = classLoader;

		setIcon();

		try {
			behavior = prefs.getInt("nfcBehavior", 0);
			if (behavior != 0) {
				setListenMode();
			}
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}

	private static void setListenMode() {
		XposedHelpers.findAndHookMethod(Packages.NFC + ".NfcService",
				classLoader, "applyRouting", boolean.class,
				new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						prefs.reload();

						final int currScreenState = (Integer) XposedHelpers
								.callMethod(param.thisObject,
										"checkScreenState");
						if ((currScreenState == SCREEN_STATE_ON_UNLOCKED)
								|| (behavior == 1 && currScreenState != SCREEN_STATE_ON_LOCKED)) {
							XposedHelpers.setAdditionalInstanceField(
									param.thisObject, "mOrigScreenState", -1);
							return;
						}

						synchronized (param.thisObject) {
							XposedHelpers.setAdditionalInstanceField(
									param.thisObject, "mOrigScreenState",
									XposedHelpers.getIntField(param.thisObject,
											"mScreenState"));
							XposedHelpers.setIntField(param.thisObject,
									"mScreenState", SCREEN_STATE_ON_UNLOCKED);
						}
					}

					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						final int mOrigScreenState = (Integer) XposedHelpers
								.getAdditionalInstanceField(param.thisObject,
										"mOrigScreenState");
						if (mOrigScreenState == -1)
							return;

						synchronized (param.thisObject) {
							XposedHelpers.setIntField(param.thisObject,
									"mScreenState", mOrigScreenState);
						}
					}

				});
	}

	private static void setIcon() {
		XposedHelpers.findAndHookMethod(Packages.NFC + ".NfcService",
				classLoader, "setIcon", boolean.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {

						if ((Boolean) XposedHelpers.callMethod(
								param.thisObject, "isNfcEnabled")) {
							param.args[0] = !prefs.getBoolean("hideNfcIcon",
									false);
						}
					}
				});
	}

}
