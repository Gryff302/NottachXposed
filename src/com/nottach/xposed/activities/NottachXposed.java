package com.nottach.xposed.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nottach.xposed.R;
import com.nottach.xposed.dialogs.BatteryIconDialog;
import com.nottach.xposed.dialogs.CreditsDialog;
import com.nottach.xposed.dialogs.DisclaimerDialog;
import com.nottach.xposed.dialogs.GooWalletDialog;
import com.nottach.xposed.dialogs.KillWhiteListDialog;
import com.nottach.xposed.dialogs.MultiWindowAppsDialog;
import com.nottach.xposed.dialogs.QuickPinDialog;
import com.nottach.xposed.dialogs.QuickPinDialog.QuickPinDialogListener;
import com.nottach.xposed.dialogs.RestoreDialog;
import com.nottach.xposed.dialogs.RestoreDialog.RestoreDialogListener;
import com.nottach.xposed.dialogs.SaveDialog;
import com.nottach.xposed.notifications.RebootNotification;
import com.nottach.xposed.utils.Utils;

@SuppressWarnings("deprecation")
public class NottachXposed extends PreferenceActivity implements
		OnSharedPreferenceChangeListener, RestoreDialogListener,
		QuickPinDialogListener {

	// ColorPicker activity preferences 
	private static final String[] colorPrefKeys = new String[] {
			"statusbarColor", "clockColor", "clockDateColor",
			"batteryTextColor", "circleBatteryColor", "signalIconColor",
			"statusIconColor", "carrierLabelColor", "notificationHeaderColor",
			"notificationHeaderButtonColor", "notificationClearColor",
			"notificationTitleBarTextColor", "notificationTitleBarColor",
			"notificationNotificationColor",
			"notificationNotificationPressedColor", "notificationHandleColor",
			"notificationHandleBackgroundColor",
			"notificationHeaderButtonDividerColor",
			"notificationHandleCarrierTextColor", "quickSettingIconColor",
			"quickSettingTileColor", "quickSettingTilePressedColor",
			"quickSettingsTextLabelColor", "quickSettingsIndicatorColor",
			"autoBrightnessToggleColor", "overscrollGlowColor", "seekBarColor" };

	// NX constant for long HOME clicked app shortcut
	public static final int RECENTS_LAUNCH_APP = -1337;
	
	// System constants for long HOME clicked
	public static final int RECENTS_NO_ACTION = 0; // Do nothing
	public static final int RECENTS_ICONS = 1; // Recent apps (Gingerbread style)
	public static final int RECENTS_THUMBNAIL = 2; // Recent apps (ICS Style)
	public static final int RECENTS_GOOGLE_NOW = 3; // Launch Google Now
	
	// NX constants for onActivityResult requests
	protected static final int COLOR_REQUEST = 30706;
	protected static final int NOTI_PANEL_BG_PHOTO_REQUEST = 30707;

	// Fields
	private List<String> changesMade;
	private int mOldColor;
	private boolean mOldEnabled;
	private PreferenceScreen prefsRoot;
	private CheckBoxPreference quickPinPref;

	private OnPreferenceClickListener colorListener = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			startColorPickerActivityForResult(preference);
			return true;
		}
	};

	private OnPreferenceClickListener batteryIconListener = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(final Preference preference) {
			new BatteryIconDialog().show(getFragmentManager(), "batteryIcon");
			return true;
		}
	};

	private OnPreferenceClickListener panelBackgroundListener = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(final Preference preference) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					NottachXposed.this);
			builder.setCancelable(true).setTitle(R.string.panel_background)
					.setNegativeButton("Disable", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							getPreferenceManager()
									.getSharedPreferences()
									.edit()
									.putBoolean(
											"notificationPanelBackgroundEnabled",
											false).commit();
						}
					}).setNeutralButton("Image", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent photoIntent = new Intent();
							photoIntent.setType("image/*");
							photoIntent.setAction(Intent.ACTION_GET_CONTENT);
							photoIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
							startActivityForResult(photoIntent,
									NOTI_PANEL_BG_PHOTO_REQUEST);
						}
					}).setPositiveButton("Color", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							startColorPickerActivityForResult(preference);
						}
					}).create().show();

			return true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		changesMade = new ArrayList<String>();

		// Check for AT&T Device
		if (!"jflteuc".equals(Build.PRODUCT)) {
			new DisclaimerDialog().show(getFragmentManager(), "disclaimer"); // Show disclaimer dialog
		}

		getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE); // Readable by all packages (required)
		addPreferencesFromResource(R.xml.prefs);

		prefsRoot = ((PreferenceScreen) findPreference("prefsRoot")); // Root preference screen
		if (Utils.isMintJelly()) {
			prefsRoot.removePreference(findPreference("notAttDisclaimer")); // If not Mint Jelly ROM show text preference
		}

		findPreference("clockDateApplication").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						startAppChooserActivity("clockDateApplicationUri",
								"clockDateApplicationLabel");
						return true;
					}
				});

		findPreference("clockDateLongApplication")
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						startAppChooserActivity("clockDateLongApplicationUri",
								"clockDateLongApplicationLabel");
						return true;
					}
				});

		findPreference("longHomePressBehaviour").setOnPreferenceChangeListener(
				new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						int selection = Integer.parseInt((String) newValue);
						if (selection == RECENTS_LAUNCH_APP) {
							startAppChooserActivity("homeLongApplicationUri",
									"homeLongApplicationLabel");
						}
						return true;
					}
				});

		findPreference("doubleHomeClickApplication")
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								NottachXposed.this);
						dialog.setCancelable(true)
								.setTitle(preference.getTitle())
								.setNeutralButton("Application",
										new OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												startAppChooserActivity(
														"doubleHomeClickApplicationUri",
														"doubleHomeClickApplicationLabel");
											}
										})
								.setPositiveButton("Recent Apps",
										new OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												PreferenceManager
														.getDefaultSharedPreferences(
																NottachXposed.this)
														.edit()
														.putBoolean(
																"doubleHomeClickIsRecents",
																true).commit();
											}
										}).create().show();

						return true;
					}
				});

		findPreference("enableLongBackKillWhiteList")
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						new KillWhiteListDialog().show(getFragmentManager(),
								"killWhiteList");
						return true;
					}
				});

		findPreference("selectMwApps").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						new MultiWindowAppsDialog().show(getFragmentManager(),
								"multiWindowApps");
						return true;
					}
				});

		findPreference("selectedBatteryIcon").setOnPreferenceClickListener(
				batteryIconListener);

		findPreference("notificationPanelBackground")
				.setOnPreferenceClickListener(panelBackgroundListener);

		// Set the color preference listeners
		for (String colorPrefKey : colorPrefKeys) {
			findPreference(colorPrefKey).setOnPreferenceClickListener(
					colorListener);
		}

		// Build config preferences
		EditTextPreference boardPref = (EditTextPreference) findPreference("buildprop_board");
		boardPref.setDialogMessage("AT&T Default:\nMSM8960");
		boardPref.getEditText().setHint(Build.BOARD);
		EditTextPreference brandPref = (EditTextPreference) findPreference("buildprop_brand");
		brandPref.setDialogMessage("AT&T Default:\nsamsung");
		brandPref.getEditText().setHint(Build.BRAND);
		EditTextPreference devicePref = (EditTextPreference) findPreference("buildprop_device");
		devicePref.setDialogMessage("AT&T Default:\njflteatt");
		devicePref.getEditText().setHint(Build.DEVICE);
		EditTextPreference modelPref = (EditTextPreference) findPreference("buildprop_model");
		modelPref.setDialogMessage("AT&T Default:\nSAMSUNG-SGH-I337");
		modelPref.getEditText().setHint(Build.MODEL);
		EditTextPreference namePref = (EditTextPreference) findPreference("buildprop_name");
		namePref.setDialogMessage("AT&T Default:\njflteuc");
		namePref.getEditText().setHint(Build.PRODUCT);

		findPreference("applyGoogleWalletHack").setOnPreferenceChangeListener(
				new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if ((Boolean) newValue) {
							new GooWalletDialog().show(getFragmentManager(),
									"gooWallet");
						}
						return true;
					}
				});

		// Handle Mint Jelly ROM preference screen
		PreferenceScreen mjPrefs = ((PreferenceScreen) findPreference(getString(R.string.mint_jelly)));
		if (!Utils.isMintJelly()) {
			prefsRoot.removePreference(mjPrefs); // If not Mint Jelly ROM
		} else {
			mjPrefs.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {
					startActivity(new Intent(NottachXposed.this,
							MintJelly.class));
					return false;
				}
			});
		}

		// Secret torch lock screen controls preference
		findPreference("launchTorchActivity").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						Intent intentDeviceTest = new Intent(
								"android.intent.action.MAIN");
						intentDeviceTest
								.setComponent(new ComponentName(
										"com.android.settings",
										"com.android.settings.torchlight.TorchlightSettings"));
						startActivity(intentDeviceTest);
						return true;
					}
				});

		// Quick PIN unlock preference
		quickPinPref = (CheckBoxPreference) findPreference("quickPinUnlockEnabled");
		quickPinPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if ((Boolean) newValue) {
							new QuickPinDialog().show(getFragmentManager(),
									"quickPin");
						}
						return true;
					}
				});

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_credits:
			new CreditsDialog().show(getFragmentManager(), "credits");
			break;
		case R.id.action_save:
			new SaveDialog().show(getFragmentManager(), "save");
			break;
		case R.id.action_restore:
			new RestoreDialog().show(getFragmentManager(), "restore");
			break;

		default:
			break;
		}
		return true;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case COLOR_REQUEST: // Handle color picker activity result
				String key = data.getStringExtra("key");
				int newColor = data.getIntExtra("color", Color.WHITE);
				if (mOldColor != newColor) {
					getPreferenceManager().getSharedPreferences().edit()
							.putInt(key, newColor).commit();
				}
				boolean enabled = data.getBooleanExtra("enabled", false);
				if (mOldEnabled != enabled) {
					getPreferenceManager().getSharedPreferences().edit()
							.putBoolean(key + "Enabled", enabled).commit();
				}
				if (key.equals("notificationPanelBackground")) {
					getPreferenceManager()
							.getSharedPreferences()
							.edit()
							.putBoolean("notificationPanelBackgroundIsColor",
									true).commit();
				}

				break;
			case NOTI_PANEL_BG_PHOTO_REQUEST: // Handle notification panel image background chooser result
				Uri selectedImage = data.getData();
				getPreferenceManager()
						.getSharedPreferences()
						.edit()
						.putString("notificationPanelBackgroundImageUri",
								selectedImage.toString()).commit();
				getPreferenceManager().getSharedPreferences().edit()
						.putBoolean("notificationPanelBackgroundEnabled", true)
						.commit();
				getPreferenceManager()
						.getSharedPreferences()
						.edit()
						.putBoolean("notificationPanelBackgroundIsColor", false)
						.commit();
				new AlertDialog.Builder(NottachXposed.this)
						.setTitle(R.string.panel_background_changed)
						.setCancelable(true)
						.setMessage(R.string.full_reboot_required)
						.setNegativeButton(R.string.reboot,
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										sendBroadcast(new Intent(
												"com.nottach.xposed.action.REBOOT_DEVICE"));
									}
								})
						.setPositiveButton(android.R.string.ok,
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create().show();
				break;

			default:
				break;
			}
		}

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
		
		// No reboot notification required
		String[] litePrefs = new String[] { "appChooserShowAllActivities" };
		for (String string : litePrefs) {
			if (key.equals(string)) {
				return;
			}
		}
		
		// Add preference key to changed keys list
		if (!changesMade.contains(key)) {
			changesMade.add(key);
		}
		
		if (changesMade.contains("notificationPanelBackgroundImageUri")) {
			RebootNotification.notify(this, changesMade.size(), false); // Params: (Context, Number, Show Soft Reboot)
		} else {
			RebootNotification.notify(this, changesMade.size(), true);
		}

	}

	// Handle color preference - launch color picker activity for result
	private void startColorPickerActivityForResult(Preference preference) {
		Intent colorIntent = new Intent(NottachXposed.this,
				NottachColorPicker.class);
		Bundle bundle = new Bundle();
		bundle.putString("title", preference.getTitle().toString());
		bundle.putString("key", preference.getKey());
		mOldColor = getPreferenceManager().getSharedPreferences().getInt(
				preference.getKey(), Color.parseColor("#ff33b5e5"));
		bundle.putInt("color", mOldColor);
		mOldEnabled = getPreferenceManager().getSharedPreferences().getBoolean(
				preference.getKey() + "Enabled", false);
		bundle.putBoolean("enabled", mOldEnabled);
		colorIntent.putExtras(bundle);
		startActivityForResult(colorIntent, COLOR_REQUEST);
	}

	// Launch application shortcut chooser activity
	private void startAppChooserActivity(String uri, String label) {
		Bundle b = new Bundle();
		b.putString(NottachAppChooser.APP_URI, uri);
		b.putString(NottachAppChooser.APP_LABEL, label);
		Intent intent = new Intent(NottachXposed.this, NottachAppChooser.class);
		intent.putExtra("pref", b);
		startActivity(intent);
	}

	@Override
	public void onRestoreDefaults() {
		getPreferenceManager().getSharedPreferences().edit().clear().commit();
		PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
		Toast.makeText(this, R.string.defaults_restored, Toast.LENGTH_SHORT)
				.show();
		RebootNotification.notify(this, 999, false);
	}

	@Override
	public void onRestoreBackup(final File backup) {
		new RestoreBackupTask(backup).execute();
	}

	class RestoreBackupTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progressDialog;
		private File backup;

		public RestoreBackupTask(File backup) {
			this.backup = backup;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			unregisterPrefsReceiver();
			progressDialog = new ProgressDialog(NottachXposed.this);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage(getString(R.string.restoring_backup));
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			ObjectInputStream input = null;
			try {
				input = new ObjectInputStream(new FileInputStream(backup));
				Editor prefEdit = getPreferenceManager().getSharedPreferences()
						.edit();
				prefEdit.clear();
				@SuppressWarnings("unchecked")
				Map<String, ?> entries = (Map<String, ?>) input.readObject();
				for (Entry<String, ?> entry : entries.entrySet()) {
					Object v = entry.getValue();
					String key = entry.getKey();

					if (v instanceof Boolean)
						prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
					else if (v instanceof Float)
						prefEdit.putFloat(key, ((Float) v).floatValue());
					else if (v instanceof Integer)
						prefEdit.putInt(key, ((Integer) v).intValue());
					else if (v instanceof Long)
						prefEdit.putLong(key, ((Long) v).longValue());
					else if (v instanceof String)
						prefEdit.putString(key, ((String) v));
				}
				prefEdit.commit();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (input != null) {
						input.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			SystemClock.sleep(1500);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			Toast.makeText(NottachXposed.this, R.string.backup_restored,
					Toast.LENGTH_SHORT).show();
			RebootNotification.notify(NottachXposed.this, 999, false);
			registerPrefsReceiver();
		}

	}

	@Override
	public void onQuickPinDialogCancelled() {
		Log.e(getClass().getSimpleName(), "onQuickPinDialogCancelled Received");
		quickPinPref.setChecked(false);
	}

}
