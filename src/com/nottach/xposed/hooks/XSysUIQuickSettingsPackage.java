package com.nottach.xposed.hooks;

import java.lang.reflect.Method;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.StateSet;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nottach.xposed.utils.Packages;
import com.nottach.xposed.utils.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XSysUIQuickSettingsPackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XSysUIQuickSettingsPackage.prefs = prefs;
		XSysUIQuickSettingsPackage.classLoader = classLoader;

		if (prefs.getBoolean("quickSettingsCollapseOnToggle", false)) {
			enableCollapseOnToggle();
		}

		if (prefs.getBoolean("quickSettingTileColorEnabled", false)) {
			setToggleSliderBackground();
		}

		if (prefs.getBoolean("quickSettingIconColorEnabled", false)) {
			setIconHue();
			setBrightnessSliderColors();
		}

		handleIndicatorBars();

		handleTextLabels();

		if (prefs.getBoolean("disableAirplaneModeDialog", false)) {
			disableAirplaneModeDialog();
		}

		if (prefs.getBoolean("brightnessIconTorchToggle", false)) {
			enableTorchToggle();
		}

	}

	private static void enableTorchToggle() {
		Class<?> brightnessController = XposedHelpers.findClass(
				Packages.SYSTEM_UI + ".statusbar.policy.BrightnessController",
				classLoader);
		XposedBridge.hookAllConstructors(brightnessController,
				new XC_MethodHook() {
					@SuppressWarnings("deprecation")
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						View view = null;
						if (prefs.getBoolean("useAnimatedBrightnessIcon", true)) {
							view = (View) XposedHelpers.getObjectField(
									param.thisObject, "mAnimIcon");
						} else {
							view = (View) XposedHelpers.getObjectField(
									param.thisObject, "mIcon");
						}
						view.setClickable(true);
						StateListDrawable drawable = new StateListDrawable();
						drawable.addState(
								new int[] { android.R.attr.state_pressed },
								new ColorDrawable(prefs.getInt(
										"quickSettingIconColor",
										Color.parseColor("#ff33b5e5"))));
						drawable.addState(StateSet.WILD_CARD,
								new ColorDrawable(Color.TRANSPARENT));

						view.setBackgroundDrawable(drawable);
						view.setOnLongClickListener(new OnLongClickListener() {

							@Override
							public boolean onLongClick(View v) {
								v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
								v.getContext()
										.sendBroadcast(
												new Intent(
														"com.nottach.xposed.action.TORCH"));
								try {
									Utils.closeStatusBar(v.getContext());
								} catch (Throwable e) {
									e.printStackTrace();
								}
								return true;
							}
						});

					}

				});
	}

	private static void setIconHue() {
		String[] views = new String[] { "mBtnImage" };
		final int iconColor = prefs.getInt("quickSettingIconColor",
				Color.parseColor("#ff33b5e5"));
		for (final String string : views) {
			XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
					+ ".statusbar.policy.quicksetting.QuickSettingButton",
					classLoader, "setActivateStatus", int.class,
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param)
								throws Throwable {
							((ImageView) XposedHelpers.getObjectField(
									param.thisObject, string)).setColorFilter(
									iconColor, PorterDuff.Mode.MULTIPLY);
						}
					});
		}
	}

	private static void handleIndicatorBars() {
		Method qQuickSettingButton = XposedHelpers.findMethodExact(
				XposedHelpers.findClass(Packages.SYSTEM_UI
						+ ".statusbar.policy.quicksetting.QuickSettingButton",
						classLoader), "setActivateStatus", int.class);
		XposedBridge.hookMethod(qQuickSettingButton, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param)
					throws Throwable {
				ImageView mBtnLED = ((ImageView) XposedHelpers.getObjectField(
						param.thisObject, "mBtnLED"));
				if (prefs.getBoolean("quickSettingsHideIndicators", false)) {
					mBtnLED.setVisibility(View.GONE);
				}
				if (prefs.getBoolean("quickSettingsIndicatorColorEnabled",
						false)) {
					mBtnLED.setColorFilter(
							prefs.getInt("quickSettingsIndicatorColor",
									Color.parseColor("#ff33b5e5")),
							PorterDuff.Mode.MULTIPLY);
				}
			}
		});
	}

	private static void handleTextLabels() {
		Method quickSettingButton = XposedHelpers.findMethodExact(XposedHelpers
				.findClass(Packages.SYSTEM_UI
						+ ".statusbar.policy.quicksetting.QuickSettingButton",
						classLoader), "setActivateStatus", int.class);
		XposedBridge.hookMethod(quickSettingButton, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param)
					throws Throwable {
				TextView mBtnText = ((TextView) XposedHelpers.getObjectField(
						param.thisObject, "mBtnText"));
				if (prefs.getBoolean("quickSettingsHideTextLabels", false)) {
					mBtnText.setVisibility(View.GONE);
				}
				if (prefs.getBoolean("quickSettingsTextLabelColorEnabled",
						false)) {
					mBtnText.setTextColor(prefs.getInt(
							"quickSettingsTextLabelColor", Color.WHITE));
				}
			}
		});

	}

	private static void enableCollapseOnToggle() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.policy.quicksetting.QuickSettingButton",
				classLoader, "onClick", View.class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						Utils.closeStatusBar(((View) param.args[0])
								.getContext());
					}
				});
	}

	private static void setToggleSliderBackground() {
		XposedHelpers.findAndHookMethod(Packages.SYSTEM_UI
				+ ".statusbar.policy.ToggleSlider", classLoader, "setValue",
				int.class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						((RelativeLayout) param.thisObject)
								.setBackgroundColor(prefs.getInt(
										"quickSettingTileColor", Color.BLACK));
					}
				});
	}

	private static void disableAirplaneModeDialog() {
		XposedHelpers
				.findAndHookMethod(
						Packages.SYSTEM_UI
								+ ".statusbar.policy.quicksetting.AirplaneModeQuickSettingButton",
						classLoader, "showConfirmPopup", boolean.class,
						new XC_MethodReplacement() {
							@Override
							protected Object replaceHookedMethod(
									MethodHookParam param) throws Throwable {
								Object[] arrayOfObject = new Object[1];
								arrayOfObject[0] = param.args[0];
								XposedHelpers.callMethod(param.thisObject,
										"setAirplaneMode", arrayOfObject);
								XposedHelpers.setBooleanField(param.thisObject,
										"mIsProcessing", true);

								return null;
							}
						});
	}

	private static void setBrightnessSliderColors() {
		Class<?> classToggleSlider = XposedHelpers.findClass(Packages.SYSTEM_UI
				+ ".statusbar.policy.ToggleSlider", classLoader);
		if (prefs.getBoolean("quickSettingsTextLabelColorEnabled", false)) {
			XposedBridge.hookAllConstructors(classToggleSlider,
					new XC_MethodHook() {

						@Override
						protected void afterHookedMethod(MethodHookParam param)
								throws Throwable {
							TextView mLabel = (TextView) XposedHelpers
									.getObjectField(param.thisObject, "mLabel");
							mLabel.setTextColor(prefs.getInt(
									"quickSettingsTextLabelColor", Color.WHITE));
						}
					});
		}
		if (prefs.getBoolean("useAnimatedBrightnessIcon", true)) {
			Class<?> classAnimatedBrightnessIconView = XposedHelpers.findClass(
					Packages.SYSTEM_UI
							+ ".statusbar.AnimatedBrightnessIconView",
					classLoader);
			XposedHelpers.findAndHookMethod(classAnimatedBrightnessIconView,
					"onDraw", Canvas.class, new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param)
								throws Throwable {
							Paint paint = (Paint) XposedHelpers.getObjectField(
									param.thisObject, "mPaint");
							paint.setColor(prefs.getInt(
									"quickSettingIconColor",
									Color.parseColor("#ff33b5e5")));

						}
					});
		} else {
			XposedHelpers.findAndHookMethod(classToggleSlider, "updateIcon",
					boolean.class, int.class, new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param)
								throws Throwable {
							ImageView imageView = (ImageView) XposedHelpers
									.getObjectField(param.thisObject,
											"mBrightnessIcon");
							imageView.setColorFilter(
									prefs.getInt("quickSettingIconColor",
											Color.parseColor("#ff33b5e5")),
									PorterDuff.Mode.MULTIPLY);

						}
					});
		}

	}

}
