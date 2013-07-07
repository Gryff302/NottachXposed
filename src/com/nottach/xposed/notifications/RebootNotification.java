package com.nottach.xposed.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.nottach.xposed.R;
import com.nottach.xposed.activities.NottachXposed;

public class RebootNotification {

	private static final String NOTIFICATION_TAG = "RebootNotification";

	private static int number = 0;

	public static void notify(final Context context, final int n,
			boolean showSoftReboot) {
		number = n;

		final Resources res = context.getResources();

		final Bitmap picture = BitmapFactory
				.decodeResource(res, R.drawable.dev);

		final String ticker = res.getString(R.string.reboot_required);
		final String title = res.getString(R.string.reboot_required_title);
		final String text = res.getString(R.string.reboot_required_message);

		final NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context)
				.setDefaults(0)
				.setSmallIcon(R.drawable.ic_stat_reboot)
				.setContentTitle(title)
				.setContentText(text)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setLargeIcon(picture)
				.setTicker(ticker)
				.setNumber(number)
				.setWhen(0)
				.setContentIntent(
						PendingIntent.getActivity(context, 0, new Intent(
								context, NottachXposed.class),
								PendingIntent.FLAG_UPDATE_CURRENT))
				.setStyle(
						new NotificationCompat.BigTextStyle().bigText(text)
								.setBigContentTitle(title)
								.setSummaryText("Changes pending"))
				.addAction(
						android.R.drawable.ic_menu_rotate,
						res.getString(R.string.reboot),
						PendingIntent.getBroadcast(context, 1337, new Intent(
								"com.nottach.xposed.action.REBOOT_DEVICE"),
								PendingIntent.FLAG_UPDATE_CURRENT))
				.setAutoCancel(true);
		if (showSoftReboot) {
			builder.addAction(android.R.drawable.ic_menu_rotate, res
					.getString(R.string.soft_reboot), PendingIntent
					.getBroadcast(context, 1337, new Intent(
							"com.nottach.xposed.action.SOFT_REBOOT_DEVICE"),
							PendingIntent.FLAG_UPDATE_CURRENT));
		}

		notify(context, builder.build());
	}

	private static void notify(final Context context,
			final Notification notification) {
		final NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(NOTIFICATION_TAG, 0, notification);
	}

	public static void cancel(final Context context) {
		final NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_TAG, 0);
	}

	public static int getNumber() {
		return number;
	}
}