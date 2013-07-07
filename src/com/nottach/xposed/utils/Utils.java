package com.nottach.xposed.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.widget.TextView;
import de.robv.android.xposed.XSharedPreferences;

public class Utils {

	public static void closeStatusBar(Context context) throws Throwable {
		Object sbservice = context.getSystemService("statusbar");
		Class<?> statusbarManager = Class
				.forName("android.app.StatusBarManager");
		Method showsb = statusbarManager.getMethod("collapsePanels");
		showsb.invoke(sbservice);
	}

	public static String softReboot(Context context) {
		return executeScript(context, "soft_reboot.sh");
	}

	public static String reboot(Context context) {
		return executeScript(context, "reboot.sh");
	}

	private static String executeScript(Context context, String name) {
		File scriptFile = writeAssetToCacheFile(context, name);
		if (scriptFile == null)
			return "Could not find asset \"" + name + "\"";

		File busybox = writeAssetToCacheFile(context, "busybox-xposed");
		if (busybox == null) {
			scriptFile.delete();
			return "Could not find asset \"busybox-xposed\"";
		}

		scriptFile.setReadable(true, false);
		scriptFile.setExecutable(true, false);

		busybox.setReadable(true, false);
		busybox.setExecutable(true, false);

		try {
			Process p = Runtime.getRuntime().exec(
					new String[] {
							"su",
							"-c",
							scriptFile.getAbsolutePath() + " "
									+ android.os.Process.myUid() + " 2>&1" },
					null, context.getCacheDir());
			BufferedReader stdout = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = stdout.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			stdout.close();
			return sb.toString();

		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			return sw.toString();
		} finally {
			scriptFile.delete();
			busybox.delete();
		}
	}

	private static File writeAssetToCacheFile(Context context, String name) {
		return writeAssetToCacheFile(context, name, name);
	}

	private static File writeAssetToCacheFile(Context context,
			String assetName, String fileName) {
		File file = null;
		try {
			InputStream in = context.getAssets().open(assetName);
			file = new File(context.getCacheDir(), fileName);
			FileOutputStream out = new FileOutputStream(file);

			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();

			return file;
		} catch (IOException e) {
			e.printStackTrace();
			if (file != null)
				file.delete();

			return null;
		}
	}

	public static TextView setTypeface(XSharedPreferences prefs, TextView tv) {

		int typeStyle = Typeface.NORMAL;
		if (!prefs.getString("statusbarTextStyle", "Normal").equals("Normal")) {
			if (prefs.getString("statusbarTextStyle", "Normal")
					.equals("Italic")) {
				typeStyle = Typeface.ITALIC;
			} else if (prefs.getString("statusbarTextStyle", "Normal").equals(
					"Bold")) {
				typeStyle = Typeface.BOLD;
			}
		}
		String typeFace = "sans-serif";
		if (!prefs.getString("statusbarTextFace", "Regular").equals("Regluar")) {
			if (prefs.getString("statusbarTextFace", "Regular").equals("Light")) {
				typeFace = "sans-serif-light";
			}
			if (prefs.getString("statusbarTextFace", "Regular").equals(
					"Condensed")) {
				typeFace = "sans-serif-condensed";
			}
			if (prefs.getString("statusbarTextFace", "Regular").equals("Thin")) {
				typeFace = "sans-serif-thin";
			}

		}
		tv.setTypeface(Typeface.create(typeFace, typeStyle));

		return tv;

	}

	public static boolean isMintJelly() {
		if (Build.DISPLAY.substring(0, 2).equals("MJ")) {
			return true;
		}
		return false;
	}

	public static int getMintJellyVersion() {
		return Integer.parseInt(Build.DISPLAY.substring(2, 3));
	}

}
