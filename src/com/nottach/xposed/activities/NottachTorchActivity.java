package com.nottach.xposed.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ToggleButton;

import com.nottach.xposed.R;

public class NottachTorchActivity extends Activity {

	static Camera mCamera;
	static Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Window window = this.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		window.setWindowAnimations(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		RelativeLayout.LayoutParams dialogParams = new LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		LinearLayout linearLayout = new LinearLayout(this);
		LayoutParams toggleParams = new LayoutParams(600, 300);

		final ToggleButton toggleButton = new ToggleButton(this);
		toggleButton.setLayoutParams(toggleParams);
		toggleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (toggleButton.isChecked()) {
					ledOn();
					toggleButton.setText(R.string.torch_is_on);
				} else if (!toggleButton.isChecked()) {
					ledOff();
					toggleButton.setText(R.string.torch_is_off);
				}
			}
		});

		linearLayout.addView(toggleButton);

		dialog.addContentView(linearLayout, dialogParams);

		toggleButton.setChecked(true);
		toggleButton.setText(R.string.torch_is_on);
		ledOn();

		dialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (toggleButton.isChecked()) {
					ledOff();
				}
				if (dialog != null) {
					dialog.dismiss();
					finish();
				}
			}
		});

		dialog.show();

	}

	public boolean ledOn() {
		mCamera = Camera.open();
		Parameters parameters = mCamera.getParameters();
		parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		mCamera.setParameters(parameters);
		mCamera.startPreview();
		mCamera.autoFocus(new AutoFocusCallback() {
			public void onAutoFocus(boolean success, Camera camera) {
			}
		});
		return true;
	}

	public boolean ledOff() {
		mCamera.stopPreview();
		mCamera.release();
		return false;
	}

}
