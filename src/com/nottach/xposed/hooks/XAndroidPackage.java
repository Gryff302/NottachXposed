package com.nottach.xposed.hooks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class XAndroidPackage {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;
	private static Intent doubleClickHomeIntent;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XAndroidPackage.prefs = prefs;
		XAndroidPackage.classLoader = classLoader;

		if (prefs.getBoolean("quickPinUnlockEnabled", false)) {
			enableQuickUnlock();
		}

		if (prefs.getBoolean("buildprop_enable", false)) {
			XBuildProp.doHook(prefs, classLoader);
		}

		setDoubleClickHome();

		if (prefs.getBoolean("disableLoudVolumeWarning", false)) {
			disableLoudVolumeWarningDialog();
		}

		if (prefs.getBoolean("hideSmartStayIcon", false)) {
			hideSmartStayIcon();
		}

		setLockscreenCarrierText();

		if (prefs.getBoolean("hideEmergencyCallButton", false)) {
			hideEmergencyCallButton();
		}

		if (prefs.getBoolean("disableTetherProvisioning", false)) {
			enableBluetoothTethering();
		}

		handlePhoneWindowManager();

		if (prefs.getBoolean("autoExpandVolumePanel", false)) {
			setExpandedVolumePanel();
		}

	}

	private static void setExpandedVolumePanel() {
		final Class<?> VolumePanel = XposedHelpers.findClass(
				"android.view.VolumePanel", classLoader);
		XposedHelpers.findAndHookMethod(VolumePanel, "onVolumeChanged",
				int.class, int.class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						if (!(Boolean) XposedBridge.invokeOriginalMethod(
								XposedHelpers.findMethodBestMatch(VolumePanel,
										"isExpanded"), param.thisObject, null)) {
							ImageView mMoreButton = (ImageView) XposedHelpers
									.getObjectField(param.thisObject,
											"mMoreButton");
							mMoreButton.performClick();
						}
					}
				});
	}

	private static void enableQuickUnlock() {

		Class<?> KeyguardPINView = XposedHelpers.findClass(
				"com.android.internal.policy.impl.keyguard.KeyguardPINView",
				classLoader);
		Class<?> KeyguardAbsKeyInputView = XposedHelpers
				.findClass(
						"com.android.internal.policy.impl.keyguard.KeyguardAbsKeyInputView",
						classLoader);
		final Method verifyPasswordAndUnlock = XposedHelpers.findMethodExact(
				KeyguardAbsKeyInputView, "verifyPasswordAndUnlock");
		XposedHelpers.findAndHookMethod(KeyguardPINView, "onFinishInflate",
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(final MethodHookParam param)
							throws Throwable {
					}
				});
		XposedHelpers.findAndHookMethod(KeyguardPINView, "onFinishInflate",
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(final MethodHookParam param)
							throws Throwable {
						final TextView mPasswordEntry = (TextView) XposedHelpers
								.getObjectField(param.thisObject,
										"mPasswordEntry");
						mPasswordEntry
								.addTextChangedListener(new TextWatcher() {

									@Override
									public void onTextChanged(CharSequence s,
											int start, int before, int count) {
									}

									@Override
									public void beforeTextChanged(
											CharSequence s, int start,
											int count, int after) {

									}

									@Override
									public void afterTextChanged(Editable s) {
										if (s.length() >= prefs.getInt(
												"quickPinUnlockLength", 4)) {
											try {
												XposedBridge
														.invokeOriginalMethod(
																verifyPasswordAndUnlock,
																param.thisObject,
																null);
											} catch (NullPointerException e) {
												e.printStackTrace();
											} catch (IllegalArgumentException e) {
												e.printStackTrace();
											} catch (IllegalAccessException e) {
												e.printStackTrace();
											} catch (InvocationTargetException e) {
												e.printStackTrace();
											}
										}

									}
								});
					}
				});
	}

	private static void handlePhoneWindowManager() {
		final Class<?> PhoneWindowManager = XposedHelpers.findClass(
				"com.android.internal.policy.impl.PhoneWindowManager",
				classLoader);

		if (prefs.getBoolean("disableWakeOnHome", false)) {
			XposedHelpers.findAndHookMethod(PhoneWindowManager,
					"isWakeKeyWhenScreenOff", int.class, new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param)
								throws Throwable {
							int keyCode = (Integer) param.args[0];
							if (keyCode == KeyEvent.KEYCODE_HOME) {
								param.setResult(Boolean.FALSE);
							}
						}
					});

		}
		if (prefs.getBoolean("enableLongBackKill", false)) {
			Object[] arrayOfObject = new Object[4];
			arrayOfObject[0] = "android.view.WindowManagerPolicy.WindowState";
			arrayOfObject[1] = KeyEvent.class;
			arrayOfObject[2] = int.class;
			arrayOfObject[3] = new XC_MethodHook() {
				private boolean mIsMultiWindowEnabled;
				private int mEnableMultiWindowUISetting;

				@Override
				protected void beforeHookedMethod(MethodHookParam param)
						throws Throwable {
					mIsMultiWindowEnabled = XposedHelpers.getBooleanField(
							param.thisObject, "mIsMultiWindowEnabled");
					mEnableMultiWindowUISetting = XposedHelpers.getIntField(
							param.thisObject, "mEnableMultiWindowUISetting");
					XposedHelpers.setBooleanField(param.thisObject,
							"mIsMultiWindowEnabled", true);
					XposedHelpers.setIntField(param.thisObject,
							"mEnableMultiWindowUISetting", 1);
				}

				@Override
				protected void afterHookedMethod(MethodHookParam param)
						throws Throwable {
					XposedHelpers.setBooleanField(param.thisObject,
							"mIsMultiWindowEnabled", mIsMultiWindowEnabled);
					XposedHelpers.setIntField(param.thisObject,
							"mEnableMultiWindowUISetting",
							mEnableMultiWindowUISetting);
				}
			};
			XposedHelpers.findAndHookMethod(PhoneWindowManager,
					"interceptKeyBeforeDispatching", arrayOfObject);

			XposedHelpers.findAndHookMethod(PhoneWindowManager,
					"toggleMultiWindowTray", new XC_MethodReplacement() {

						@Override
						protected Object replaceHookedMethod(
								MethodHookParam param) throws Throwable {
							final Context mContext = (Context) XposedHelpers
									.getObjectField(param.thisObject,
											"mContext");
							ActivityManager activityManager = (ActivityManager) mContext
									.getSystemService(Context.ACTIVITY_SERVICE);
							List<RunningTaskInfo> tasks = activityManager
									.getRunningTasks(1);
							RunningTaskInfo info = tasks.get(0);

							List<String> checkedApps = new ArrayList<String>();
							String[] launchersList = Packages.LAUNCHERS_LIST;
							String[] whiteList = prefs.getString(
									"enableLongBackKillWhiteList", "").split(
									";");
							checkedApps.addAll(Arrays.asList(launchersList));
							checkedApps.addAll(Arrays.asList(whiteList));

							for (int i = 0; i < checkedApps.size(); i++) {
								String packageName = checkedApps.get(i);
								if (info.topActivity.getPackageName()
										.equalsIgnoreCase(packageName)) {
									return null;
								}
							}
							List<RunningAppProcessInfo> processes = activityManager
									.getRunningAppProcesses();
							for (final RunningAppProcessInfo runningAppProcessInfo : processes) {
								if (runningAppProcessInfo.processName
										.equalsIgnoreCase(info.topActivity
												.getPackageName())) {

									android.os.Process
											.killProcess(runningAppProcessInfo.pid);
									Intent intent = new Intent(
											"com.nottach.xposed.action.SHOW_TOAST");
									intent.putExtra("processName",
											runningAppProcessInfo.processName);
									mContext.sendBroadcast(intent);

								}

							}

							return null;
						}
					});

			final Class<?> SamsungPolicyProperties = XposedHelpers
					.findClass(
							"com.android.internal.policy.impl.sec.SamsungPolicyProperties",
							classLoader);

			XposedHelpers.findAndHookMethod(SamsungPolicyProperties,
					"getKeyPendingThresholdTime", new XC_MethodReplacement() {

						@Override
						protected Object replaceHookedMethod(
								MethodHookParam param) throws Throwable {
							return (int) 1;
						}
					});
		}

	}

	private static void hideEmergencyCallButton() {
		Class<?> classEmergencyCallButton = XposedHelpers.findClass(
				"com.android.internal.policy.impl.keyguard.EmergencyButton",
				classLoader);
		XposedHelpers.findAndHookMethod(classEmergencyCallButton,
				"onAttachedToWindow", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						((Button) param.thisObject).setVisibility(View.GONE);
					}
				});
	}

	private static void setLockscreenCarrierText() {
		Class<?> classLockCarrierText = XposedHelpers.findClass(
				"com.android.internal.policy.impl.keyguard.CarrierText",
				classLoader);
		XposedHelpers
				.findAndHookMethod(
						classLockCarrierText,
						"updateCarrierText",
						XposedHelpers
								.findClass(
										"com.android.internal.telephony.IccCardConstants.State",
										classLoader), CharSequence.class,
						CharSequence.class, new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(
									MethodHookParam param) throws Throwable {
								TextView tv = (TextView) param.thisObject;
								if (prefs.getBoolean("hideCarrierLockscreen",
										false)) {
									tv.setVisibility(View.GONE);
								}
								if (!prefs.getString("customCarrierLockscreen",
										"").equals("")) {
									tv.setText(prefs.getString(
											"customCarrierLockscreen", ""));
								}
							}
						});
	}

	private static void hideSmartStayIcon() {
		Class<?> classStatusBarManager = XposedHelpers.findClass(
				"android.app.StatusBarManager", classLoader);
		XposedHelpers.findAndHookMethod(classStatusBarManager,
				"setIconVisibility", String.class, boolean.class,
				new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						if (param.args[0].equals("smart_scroll")) {
							param.args[1] = false;
						}
					}
				});
	}

	private static void enableBluetoothTethering() {
		XposedHelpers.findAndHookMethod(
				"com.android.server.connectivity.Tethering", classLoader,
				"updateConfiguration", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						String[] btRegExs = new String[1];
						btRegExs[0] = "bnep\\d";
						XposedHelpers.setObjectField(param.thisObject,
								"mTetherableBluetoothRegexs", btRegExs);
					}
				});
	}

	private static void disableLoudVolumeWarningDialog() {
		XposedHelpers.findAndHookMethod("android.media.AudioService",
				classLoader, "isEarProtectLimitOn", new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return Boolean.FALSE;
					}
				});
	}

	private static void setDoubleClickHome() {

		try {
			if (prefs.getBoolean("doubleHomeClickIsRecents", false)) {
				Intent recentsIntent = new Intent(
						"com.android.systemui.recent.action.TOGGLE_RECENTS");
				recentsIntent
						.setComponent(ComponentName
								.unflattenFromString("com.android.systemui/com.android.systemui.recent.RecentsActivity"));
				doubleClickHomeIntent = recentsIntent;

			} else {
				String doubleClickString = prefs.getString(
						"doubleHomeClickApplicationUri", "");
				if (doubleClickString.equals("")) {
					doubleClickHomeIntent = new Intent(
							"android.intent.action.SEARCH_LONG_PRESS");
				} else {
					doubleClickHomeIntent = Intent.parseUri(doubleClickString,
							0);
				}
			}
			Object[] arrayOfObject = new Object[6];
			arrayOfObject[0] = Context.class;
			arrayOfObject[1] = "com.android.internal.policy.impl.PhoneWindowManager";
			arrayOfObject[2] = "android.view.IWindowManager";
			arrayOfObject[3] = "android.view.WindowManagerPolicy.WindowManagerFuncs";
			arrayOfObject[4] = "com.android.internal.policy.impl.keyguard.KeyguardViewMediator";
			arrayOfObject[5] = new XC_MethodHook() {
				protected void afterHookedMethod(
						XC_MethodHook.MethodHookParam param) throws Throwable {
					XposedHelpers.setObjectField(param.thisObject,
							"mVoiceTalkIntent", doubleClickHomeIntent);
				}
			};
			XposedHelpers
					.findAndHookMethod(
							"com.android.internal.policy.impl.sec.SamsungPhoneWindowManager",
							null, "init", arrayOfObject);
			return;
		} catch (Throwable localThrowable) {
			while (true)
				XposedBridge.log(localThrowable);
		}
	}

}
