package com.nottach.xposed.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.nottach.xposed.R;

public class QuickPinDialog extends DialogFragment {

	private AlertDialog dialog;
	private QuickPinDialogListener listener;

	public interface QuickPinDialogListener {
		public void onQuickPinDialogCancelled();
	}

	public QuickPinDialog() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (QuickPinDialogListener) activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity())
				.inflate(R.layout.dialog_quick_pin, null);
		final EditText editText = (EditText) layout
				.findViewById(R.id.etQuickPin);
		editText.setHint("Enter PIN");
		final EditText editTextConfirm = (EditText) layout
				.findViewById(R.id.etQuickPinConfirm);
		editTextConfirm.setHint("Confirm PIN");
		TextWatcher textWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (editTextConfirm.getText().toString().length() >= 4
						&& editTextConfirm.getText().toString()
								.equals(editText.getText().toString())) {
					dialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(true);
				} else {
					dialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
		editText.addTextChangedListener(textWatcher);
		editTextConfirm.addTextChangedListener(textWatcher);

		dialog = builder
				.setCancelable(true)
				.setTitle("Enter PIN")
				.setView(layout)
				.setPositiveButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						})
				.setNegativeButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								PreferenceManager
										.getDefaultSharedPreferences(
												getActivity())
										.edit()
										.putInt("quickPinUnlockLength",
												editTextConfirm.getText()
														.length()).commit();
							}
						}).create();

		return dialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		listener.onQuickPinDialogCancelled();
		super.onCancel(dialog);
	}

	@Override
	public void onStart() {
		super.onStart();
		dialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
	}

}
