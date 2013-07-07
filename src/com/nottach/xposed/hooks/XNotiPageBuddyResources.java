package com.nottach.xposed.hooks;

import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class XNotiPageBuddyResources {

	private static InitPackageResourcesParam resparam;
	private static XSharedPreferences prefs;
	private static String packageName;

	public static void doHook(String packageName, XSharedPreferences prefs,
			InitPackageResourcesParam resparam) {

		XNotiPageBuddyResources.packageName = packageName;
		XNotiPageBuddyResources.prefs = prefs;
		XNotiPageBuddyResources.resparam = resparam;

		if (prefs.getBoolean("statusIconColorEnabled", false)) {
			setStatusIconResources();
		}

		setHeadsetAppsNotificationLayout();
		
	}

	private static void setHeadsetAppsNotificationLayout() {
		resparam.res.hookLayout(packageName, "layout",
				"contextual_page_noti_view", new XC_LayoutInflated() {

					@Override
					public void handleLayoutInflated(LayoutInflatedParam liparam)
							throws Throwable {

						TextView titleText = (TextView) liparam.view
								.findViewById(liparam.res.getIdentifier(
										"contextualpage_quick_panel_title",
										"id", packageName));

						if (prefs.getBoolean(
								"notificationTitleBarTextColorEnabled", false)) {
							titleText.setTextColor(prefs.getInt(
									"notificationTitleBarTextColor",
									Color.WHITE));
						}
						if (prefs.getBoolean(
								"notificationTitleBarColorEnabled", false)) {

							LinearLayout titleLayout = (LinearLayout) titleText
									.getParent();
							titleLayout.setBackgroundColor(prefs.getInt(
									"notificationTitleBarColor", Color.DKGRAY));
						}
					}
				});
	}

	private static void setStatusIconResources() {
		String[] notiBuddyStatusIcons = new String[] {
				"stat_notify_desk_cradle", "stat_notify_roaming",
				"stat_notify_spen", "stat_sys_earphone", };
		for (final String string : notiBuddyStatusIcons) {
			resparam.res.setReplacement(packageName, "drawable", string,
					new XResources.DrawableLoader() {

						@Override
						public Drawable newDrawable(XResources res, int id)
								throws Throwable {
							Drawable replacement = resparam.res
									.getDrawable(resparam.res.getIdentifier(
											string, "drawable", packageName));
							replacement.setColorFilter(prefs.getInt(
									"statusIconColor", Color.WHITE),
									PorterDuff.Mode.MULTIPLY);
							return replacement;
						}
					});
		}
	}

}
