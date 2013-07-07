package com.nottach.xposed.hooks;

import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XSysScopePackage {

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		if (prefs.getBoolean("makeMeTooLegit", true)) {
			
			Object[] arrayOfObject3 = new Object[2];
			arrayOfObject3[0] = String.class;
			arrayOfObject3[1] = new XC_MethodReplacement() {
				protected Object replaceHookedMethod(
						XC_MethodHook.MethodHookParam param) throws Throwable {
					return Integer.valueOf(1);
				}
			};
			XposedHelpers.findAndHookMethod(Packages.SYS_SCOPE
					+ ".job.RootProcessScanner", classLoader,
					"checkIsApprivedProcess", arrayOfObject3);

			Object[] arrayOfObject2 = new Object[2];
			arrayOfObject2[0] = String.class;
			arrayOfObject2[1] = new XC_MethodReplacement() {
				protected Object replaceHookedMethod(
						XC_MethodHook.MethodHookParam param) throws Throwable {
					return Boolean.valueOf(true);
				}
			};
			XposedHelpers.findAndHookMethod(Packages.SYS_SCOPE
					+ ".job.KernelStatusChecker", classLoader, "b",
					arrayOfObject2);

			Object[] arrayOfObject1 = new Object[2];
			arrayOfObject1[0] = Boolean.TYPE;
			arrayOfObject1[1] = new XC_MethodHook() {
				protected void beforeHookedMethod(
						XC_MethodHook.MethodHookParam param) throws Throwable {
					param.args[0] = Boolean.valueOf(true);
				}
			};
			XposedHelpers.findAndHookMethod(Packages.SYS_SCOPE + ".engine.h",
					classLoader, "a", arrayOfObject1);

		}
	}

}
