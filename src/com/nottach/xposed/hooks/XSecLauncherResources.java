package com.nottach.xposed.hooks;

import android.content.res.XModuleResources;

import com.nottach.xposed.R;
import com.nottach.xposed.R.dimen;
import com.nottach.xposed.utils.Packages;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class XSecLauncherResources {

	public static void doHook(XSharedPreferences prefs,
			InitPackageResourcesParam resparam, XModuleResources moduleResources) {
		resparam.res.setReplacement(Packages.LAUNCHER, "bool",
				"config_fixedWallpaperOffset",
				!prefs.getBoolean("launcherFixedWallpaper", false));
		resparam.res.setReplacement(Packages.LAUNCHER, "bool",
				"opt_showHelpTextOnEmptyHomePage", false);

		if (prefs.getBoolean("launcherCompactMode", false)) {
			resizeLauncherDimens(moduleResources, resparam);
		}

		if (prefs.getBoolean("hideDockLabels", false)) {
			resparam.res.setReplacement(Packages.LAUNCHER, "bool",
					"hotseat_showTitlePermKey", false);
		}
	}

	private static void resizeLauncherDimens(XModuleResources moduleResources,
			InitPackageResourcesParam resparam) {
		resparam.res.setReplacement(Packages.LAUNCHER, "integer",
				"home_cellCountX", 5);
		resparam.res.setReplacement(Packages.LAUNCHER, "integer",
				"home_cellCountY", 5);
		resparam.res.setReplacement(Packages.LAUNCHER, "integer",
				"menuAppsGrid_cellCountX", 5);
		resparam.res.setReplacement(Packages.LAUNCHER, "integer",
				"menuAppsGrid_cellCountY", 6);
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"app_icon_size", moduleResources.fwd(R.dimen.app_icon_size));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"home_cellWidth_land",
				moduleResources.fwd(R.dimen.home_cellWidth_land));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"home_cellHeight_land",
				moduleResources.fwd(R.dimen.home_cellHeight_land));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"home_cellGapX_land",
				moduleResources.fwd(R.dimen.home_cellGapX_land));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"home_cellGapY_land",
				moduleResources.fwd(R.dimen.home_cellGapY_land));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"home_cellWidth_port",
				moduleResources.fwd(R.dimen.home_cellWidth_port));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"home_cellHeight_port",
				moduleResources.fwd(R.dimen.home_cellHeight_port));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"home_cellGapX_port",
				moduleResources.fwd(R.dimen.home_cellGapX_port));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"home_cellGapY_port",
				moduleResources.fwd(R.dimen.home_cellGapY_port));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"home_pageMarginPlusPaddingTop",
				moduleResources.fwd(R.dimen.home_pageMarginPlusPaddingTop));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"menuAppsGrid_pageMarginPlusPaddingTop", moduleResources
						.fwd(R.dimen.menuAppsGrid_pageMarginPlusPaddingTop));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"menuAppsGrid_cellWidth",
				moduleResources.fwd(R.dimen.menuAppsGrid_cellWidth));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"menuAppsGrid_cellHeight",
				moduleResources.fwd(R.dimen.menuAppsGrid_cellHeight));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"menuAppsGrid_cellGapX",
				moduleResources.fwd(R.dimen.menuAppsGrid_cellGapX));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"menuAppsGrid_cellGapY",
				moduleResources.fwd(R.dimen.menuAppsGrid_cellGapY));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"home_pageIndicatorGap",
				moduleResources.fwd(R.dimen.home_pageIndicatorGap));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"home_pageIndicatorTop",
				moduleResources.fwd(R.dimen.home_pageIndicatorTop));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"home_pageIndicatorTopShrunken",
				moduleResources.fwd(R.dimen.home_pageIndicatorTopShrunken));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"hotseat_cellWidth",
				moduleResources.fwd(R.dimen.hotseat_cellWidth));
		resparam.res.setReplacement(Packages.LAUNCHER, "dimen",
				"hotseat_cellHeight",
				moduleResources.fwd(R.dimen.hotseat_cellHeight));
	}

}
