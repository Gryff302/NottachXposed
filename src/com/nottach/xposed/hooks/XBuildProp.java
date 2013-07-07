package com.nottach.xposed.hooks;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class XBuildProp {

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {
		Class<?> classBuild = XposedHelpers.findClass("android.os.Build",
				classLoader);
		if (!prefs.getString("buildprop_board", "").equals(""))
			XposedHelpers.setStaticObjectField(classBuild, "BOARD",
					prefs.getString("buildprop_board", ""));
		if (!prefs.getString("buildprop_brand", "").equals(""))
			XposedHelpers.setStaticObjectField(classBuild, "BRAND",
					prefs.getString("buildprop_brand", ""));
		if (!prefs.getString("buildprop_device", "").equals(""))
			XposedHelpers.setStaticObjectField(classBuild, "DEVICE",
					prefs.getString("buildprop_device", ""));
		if (!prefs.getString("buildprop_model", "").equals(""))
			XposedHelpers.setStaticObjectField(classBuild, "MODEL",
					prefs.getString("buildprop_model", ""));
		if (!prefs.getString("buildprop_name", "").equals(""))
			XposedHelpers.setStaticObjectField(classBuild, "PRODUCT",
					prefs.getString("buildprop_name", ""));
	}
}
