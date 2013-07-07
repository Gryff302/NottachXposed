package com.nottach.xposed.activities;

import java.io.IOException;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.nottach.xposed.R;
import com.nottach.xposed.notifications.RebootNotification;
import com.nottach.xposed.utils.Utils;

@SuppressWarnings("deprecation")
public class MintJelly extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	protected static final int POWER_ANIM_NONE = 0;
	protected static final int POWER_ANIM_FADE = 1;
	protected static final int POWER_ANIM_CRT = 2;

	public MintJelly() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.mint_jelly);

		PreferenceScreen rootScreen = (PreferenceScreen) findPreference("mintJelly");

		handlePowerAnimPref(findPreference("powerAnimOn"));
		handlePowerAnimPref(findPreference("powerAnimOff"));

		if (Utils.getMintJellyVersion() < 2) {
			rootScreen.removePreference(findPreference("enableCallRecording"));
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		registerPrefsReceiver();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterPrefsReceiver();
	}

	private void handlePowerAnimPref(Preference preference) {
		ListPreference listPreference = (ListPreference) preference;
		listPreference.setEntries(R.array.power_anim_entries);
		listPreference.setEntryValues(R.array.power_anim_values);
		listPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						int powerAnim = Integer.parseInt((String) newValue);
						String whichPref = preference.getKey().replace(
								"powerAnim", "screen_");
						String setPropCmd = "";
						switch (powerAnim) {
						case POWER_ANIM_NONE:
							setPropCmd = "setprop persist.sys."
									+ whichPref.toLowerCase() + " none";
							break;
						case POWER_ANIM_FADE:
							setPropCmd = "setprop persist.sys."
									+ whichPref.toLowerCase() + " fade";
							break;
						case POWER_ANIM_CRT:
							setPropCmd = "setprop persist.sys."
									+ whichPref.toLowerCase() + " crt";
							break;
						default:
							break;
						}
						try {
							Runtime.getRuntime().exec(
									new String[] { "su", "-c", setPropCmd });
						} catch (IOException e) {
							e.printStackTrace();
						}
						return true;
					}
				});
	}

	private void registerPrefsReceiver() {
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	private void unregisterPrefsReceiver() {
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		RebootNotification.notify(this, 999, false);
	}

}
