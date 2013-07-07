package com.nottach.xposed.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.larswerkman.colorpicker.ColorPicker;
import com.larswerkman.colorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.colorpicker.OpacityBar;
import com.larswerkman.colorpicker.SaturationBar;
import com.larswerkman.colorpicker.ValueBar;
import com.nottach.xposed.R;

public class NottachColorPicker extends Activity implements
		OnColorChangedListener {

	private EditText editText;
	private int prefColor;
	private String prefKey;
	private Switch enabledSwitch;
	private boolean prefEnabled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		String prefTitle = bundle.getString("title");
		prefKey = bundle.getString("key");
		prefColor = bundle.getInt("color");
		prefEnabled = bundle.getBoolean("enabled");

		setTitle(prefTitle);

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);

		setContentView(R.layout.activity_color_picker);

		final ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
		OpacityBar opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
		SaturationBar saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
		ValueBar valueBar = (ValueBar) findViewById(R.id.valuebar);

		picker.addOpacityBar(opacityBar);
		picker.addSaturationBar(saturationBar);
		picker.addValueBar(valueBar);

		editText = (EditText) findViewById(R.id.edittext);

		picker.setOldCenterColor(Color.parseColor("#ff33b5e5"));
		picker.setOnColorChangedListener(this);
		picker.setColor(prefColor);

		Button bPreview = (Button) findViewById(R.id.bPreviewColor);
		bPreview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				picker.setColor(Color.parseColor("#"
						+ editText.getText().toString()));
			}
		});
		Button bApply = (Button) findViewById(R.id.bApplyColor);
		bApply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("key", prefKey);
				intent.putExtra("color", picker.getColor());
				intent.putExtra("enabled", enabledSwitch.isChecked());
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});

	}

	private void updateEdittext(String color) {
		editText.setText(color);
	}

	@Override
	public void onColorChanged(int color) {
		updateEdittext(Integer.toHexString(color));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.color_picker, menu);
		enabledSwitch = (Switch) menu.findItem(R.id.action_color_enable)
				.getActionView().findViewById(R.id.color_switch);
		enabledSwitch.setChecked(prefEnabled);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
