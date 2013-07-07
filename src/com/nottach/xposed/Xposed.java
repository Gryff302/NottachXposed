package com.nottach.xposed;

import android.content.res.XModuleResources;

import com.nottach.xposed.hooks.XAndroidPackage;
import com.nottach.xposed.hooks.XAndroidResources;
import com.nottach.xposed.hooks.XFlashBarServiceResources;
import com.nottach.xposed.hooks.XGooTalkPackage;
import com.nottach.xposed.hooks.XGooWalletPackage;
import com.nottach.xposed.hooks.XMmsPackage;
import com.nottach.xposed.hooks.XMmsResources;
import com.nottach.xposed.hooks.XNfcPackage;
import com.nottach.xposed.hooks.XNfcResources;
import com.nottach.xposed.hooks.XNotiPageBuddyPackage;
import com.nottach.xposed.hooks.XNotiPageBuddyResources;
import com.nottach.xposed.hooks.XSecEmailPackage;
import com.nottach.xposed.hooks.XSecLauncherPackage;
import com.nottach.xposed.hooks.XSecLauncherResources;
import com.nottach.xposed.hooks.XSecPhonePackage;
import com.nottach.xposed.hooks.XSecSettingsPackage;
import com.nottach.xposed.hooks.XSecVoiceResources;
import com.nottach.xposed.hooks.XSysUiPackage;
import com.nottach.xposed.hooks.XSysUiResources;
import com.nottach.xposed.hooks.XSystemWide;
import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Xposed implements IXposedHookZygoteInit,
		IXposedHookInitPackageResources, IXposedHookLoadPackage {

	private static String MODULE_PATH = null;
	private static XSharedPreferences prefs;

	@Override
	public void initZygote(StartupParam startupParam) {
		MODULE_PATH = startupParam.modulePath;
		prefs = new XSharedPreferences(Packages.NOTTACH_XPOSED);

		try {

			XSystemWide.doHook(MODULE_PATH, prefs);

		} catch (Throwable t) {
			XposedBridge.log(t);
		}

	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		prefs.reload();

		// if (lpparam.packageName.equals(Packages.SYS_SCOPE)) {
		// try {
		// XSysScopePackage.doHook(prefs, lpparam.classLoader);
		// } catch (Throwable t) {
		// XposedBridge.log(t);
		// }
		// }
		if (lpparam.packageName.equals(Packages.PHONE)) {
			try {
				XSecPhonePackage.doHook(prefs, lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (lpparam.packageName.equals(Packages.ANDROID)) {
			try {
				XAndroidPackage.doHook(prefs, lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (lpparam.packageName.equals(Packages.NFC)) {
			try {
				XNfcPackage.doHook(prefs, lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (lpparam.packageName.equals(Packages.MMS)) {
			try {
				XMmsPackage.doHook(prefs, lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (lpparam.packageName.equals(Packages.SYSTEM_UI)) {
			try {
				XSysUiPackage.doHook(prefs, lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (lpparam.packageName.equals(Packages.SETTINGS)) {
			try {
				XSecSettingsPackage.doHook(prefs, lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (lpparam.packageName.equals(Packages.NOTI_PAGE_BUDDY)
				|| lpparam.packageName.equals(Packages.NOTI_PAGE_BUDDY_SEC)) {
			try {
				XNotiPageBuddyPackage.doHook(lpparam.packageName, prefs,
						lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (lpparam.packageName.equals(Packages.LAUNCHER)) {
			try {
				XSecLauncherPackage.doHook(prefs, lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (lpparam.packageName.equals(Packages.EMAIL)) {
			try {
				XSecEmailPackage.doHook(prefs, lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (lpparam.packageName.equals(Packages.WALLET)) {
			try {
				XGooWalletPackage.doHook(prefs, lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (lpparam.packageName.equals(Packages.HANGOUTS)) {
			try {
				XGooTalkPackage.doHook(prefs, lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
	}

	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam)
			throws Throwable {
		prefs.reload();
		final XModuleResources moduleResources = XModuleResources
				.createInstance(MODULE_PATH, resparam.res);

		if (resparam.packageName.equals(Packages.ANDROID)) {
			try {
				XAndroidResources.doHook(prefs, resparam, moduleResources);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (resparam.packageName.equals(Packages.SYSTEM_UI)) {
			try {
				XSysUiResources.doHook(prefs, resparam, moduleResources);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (resparam.packageName.equals(Packages.LAUNCHER)) {
			try {
				XSecLauncherResources.doHook(prefs, resparam, moduleResources);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (resparam.packageName.equals(Packages.NFC)) {
			try {
				XNfcResources.doHook(prefs, resparam, moduleResources);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (resparam.packageName.equals(Packages.NOTI_PAGE_BUDDY)
				|| resparam.packageName.equals(Packages.NOTI_PAGE_BUDDY_SEC)) {
			try {
				XNotiPageBuddyResources.doHook(resparam.packageName, prefs,
						resparam);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (resparam.packageName.equals(Packages.S_VOICE)) {
			try {
				XSecVoiceResources.doHook(prefs, resparam, moduleResources);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (resparam.packageName.equals(Packages.MMS)) {
			try {
				XMmsResources.doHook(prefs, resparam);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		if (resparam.packageName.equals(Packages.FLASH_BAR_SERVICE)) {
			try {
				XFlashBarServiceResources.doHook(prefs, resparam,
						moduleResources);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}

	}

}
