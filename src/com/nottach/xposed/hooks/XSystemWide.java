package com.nottach.xposed.hooks;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.nottach.xposed.R;
import com.nottach.xposed.activities.NottachXposed;
import com.nottach.xposed.utils.Packages;
import com.nottach.xposed.utils.VolumeKeysSkipTrack;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class XSystemWide {

	private static String modulePath;
	private static XSharedPreferences prefs;

	public static void doHook(String modulePath, XSharedPreferences prefs) {

		XSystemWide.modulePath = modulePath;
		XSystemWide.prefs = prefs;

		setSystemWideTweaks();

		if (prefs.getBoolean("overscrollGlowColorEnabled", false)) {
			setOverscrollGlowColor();
		}

		if (prefs.getBoolean("seekBarColorEnabled", false)) {
			setSeekBarColor();
			setTwSeekBarColor();
		}

		if (prefs.getBoolean("longPressTrackSkip", false))
			VolumeKeysSkipTrack.init(false);

		setMultiWindowApps();
		enableMultiWindowMultiInstance();

		// setMasterVolumeRamp();
		// setCheckBoxColor();

	}

	@SuppressWarnings("unused")
	private static void setMasterVolumeRamp() {
		int[] mVR = new int[] { 0, 100 };
		XResources.setSystemWideReplacement(Packages.ANDROID, "array",
				"config_masterVolumeRamp", mVR);

	}

	private static void enableMultiWindowMultiInstance() {
		XResources.setSystemWideReplacement("android", "bool",
				"config_multiWindowSupportMultiInstance",
				prefs.getBoolean("enableMwMultiInstance", false));
	}

	private static void setMultiWindowApps() {
		String[] defaultApps = XResources.getSystem().getStringArray(
				XResources.getSystem().getIdentifier(
						"config_multiWindowSupportAppList", "array",
						Packages.ANDROID));
		String[] selectedApps = prefs.getString("selectedMwApps", "")
				.split(";");

		XResources.setSystemWideReplacement("android", "array",
				"config_multiWindowSupportAppList",
				combine(defaultApps, selectedApps));

	}

	private static String[] combine(String[] defaultApps, String[] selectedApps) {
		int length = defaultApps.length + selectedApps.length;
		String[] result = new String[length];
		System.arraycopy(defaultApps, 0, result, 0, defaultApps.length);
		System.arraycopy(selectedApps, 0, result, defaultApps.length,
				selectedApps.length);
		return result;
	}

	@SuppressWarnings("unused")
	private static void setCheckBoxColor() {
		XResources.setSystemWideReplacement(Packages.ANDROID, "drawable",
				"tw_btn_check_holo_dark", new XResources.DrawableLoader() {

					@Override
					public Drawable newDrawable(XResources res, int id)
							throws Throwable {
						XModuleResources moduleResources = XModuleResources
								.createInstance(modulePath, res);
						Drawable drawable = moduleResources
								.getDrawable(moduleResources.getIdentifier(
										"tw_btn_check_holo_dark", "drawable",
										Packages.NOTTACH_XPOSED));
						drawable.setColorFilter(
								prefs.getInt("checkBoxColor",
										Color.parseColor("#ff33b5e5")),
								PorterDuff.Mode.MULTIPLY);
						return drawable;
					}
				});
	}

	private static void setSystemWideTweaks() {
		XResources.setSystemWideReplacement(Packages.ANDROID, "bool",
				"config_allowAllRotations",
				prefs.getBoolean("enableAllRotation", false));
		XResources.setSystemWideReplacement(Packages.ANDROID, "bool",
				"config_enableLockScreenRotation",
				prefs.getBoolean("enableLockscreenRotation", false));
		XResources.setSystemWideReplacement(Packages.ANDROID, "bool",
				"config_unplugTurnsOnScreen",
				prefs.getBoolean("unplugScreenOn", true));
		XResources.setSystemWideReplacement(Packages.ANDROID, "bool",
				"show_ongoing_ime_switcher",
				!prefs.getBoolean("hideImeSwitcher", false));
		if (prefs.getInt("longHomePressBehaviour",
				NottachXposed.RECENTS_THUMBNAIL) != NottachXposed.RECENTS_LAUNCH_APP) {
			XResources.setSystemWideReplacement(Packages.ANDROID, "integer",
					"config_longPressOnHomeBehavior", prefs.getInt(
							"longHomePressBehaviour",
							NottachXposed.RECENTS_THUMBNAIL));
		}
		XResources.setSystemWideReplacement(Packages.ANDROID, "bool",
				"config_enableLockBeforeUnlockScreen", true);

	}

	private static void setSeekBarColor() {
		String[] progressDrawables = new String[] {
				"tw_progress_primary_holo_dark",
				"tw_progress_primary_holo_light",
				"tw_progress_secondary_holo_dark",
				"tw_progress_secondary_holo_light" };
		for (final String string : progressDrawables) {
			XResources.setSystemWideReplacement(Packages.ANDROID, "drawable",
					string, new XResources.DrawableLoader() {

						@Override
						public Drawable newDrawable(XResources res, int id)
								throws Throwable {
							XModuleResources moduleResources = XModuleResources
									.createInstance(modulePath, res);
							Drawable seekBar = moduleResources
									.getDrawable(moduleResources.getIdentifier(
											string, "drawable",
											Packages.NOTTACH_XPOSED));
							seekBar.setColorFilter(
									prefs.getInt("seekBarColor",
											Color.parseColor("#ff33b5e5")),
									PorterDuff.Mode.MULTIPLY);
							return seekBar;
						}
					});

		}
	}

	private static void setTwSeekBarColor() {
		String[] progressDrawables = new String[] {
				"tw_progress_primary_holo_dark",
				"tw_progress_secondary_holo_dark" };
		for (final String string : progressDrawables) {
			XResources.setSystemWideReplacement(Packages.TOUCH_WIZ, "drawable",
					string, new XResources.DrawableLoader() {

						@Override
						public Drawable newDrawable(XResources res, int id)
								throws Throwable {
							XModuleResources moduleResources = XModuleResources
									.createInstance(modulePath, res);
							Drawable seekBar = moduleResources
									.getDrawable(moduleResources.getIdentifier(
											string, "drawable",
											Packages.NOTTACH_XPOSED));
							seekBar.setColorFilter(
									prefs.getInt("seekBarColor",
											Color.parseColor("#ff33b5e5")),
									PorterDuff.Mode.MULTIPLY);
							return seekBar;
						}
					});

		}
	}

	private static void setOverscrollGlowColor() {
		final int overscrollColor = prefs.getInt("overscrollGlowColor",
				Color.WHITE);
		try {
			XResources.setSystemWideReplacement(Packages.ANDROID, "drawable",
					"overscroll_glow", new XResources.DrawableLoader() {

						@Override
						public Drawable newDrawable(XResources res, int id)
								throws Throwable {
							XModuleResources moduleResources = XModuleResources
									.createInstance(modulePath, res);
							Drawable oversrollGlow = moduleResources
									.getDrawable(R.drawable.overscroll_glow);
							oversrollGlow.setColorFilter(overscrollColor,
									PorterDuff.Mode.MULTIPLY);
							return oversrollGlow;
						}
					});
		} catch (Throwable t) {
			XposedBridge.log("NottachXposed: onReplaceOverScrollGlow");
			XposedBridge.log(t);
		}
		try {
			XResources.setSystemWideReplacement(Packages.ANDROID, "drawable",
					"overscroll_edge", new XResources.DrawableLoader() {

						@Override
						public Drawable newDrawable(XResources res, int id)
								throws Throwable {
							XModuleResources moduleResources = XModuleResources
									.createInstance(modulePath, res);
							Drawable oversrollEdge = moduleResources
									.getDrawable(R.drawable.overscroll_edge);
							oversrollEdge.setColorFilter(overscrollColor,
									PorterDuff.Mode.MULTIPLY);
							return oversrollEdge;
						}
					});
		} catch (Throwable t) {
			XposedBridge.log("NottachXposed: onReplaceOverScrollEdge");
			XposedBridge.log(t);
		}
	}

}
