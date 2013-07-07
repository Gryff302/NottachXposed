package com.nottach.xposed.dialogs;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.nottach.xposed.R;

public class DisclaimerDialog extends DialogFragment {

	private Dialog dialog;

	public DisclaimerDialog() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new Builder(getActivity());

		dialog = builder.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.disclaimer_title)
				.setMessage(R.string.disclaimer_message)
				.setNegativeButton(R.string.disagree, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						getActivity().finish();
					}
				}).setPositiveButton(R.string.agree, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setCancelable(true).create();
		return dialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		getActivity().finish();
	}

}
