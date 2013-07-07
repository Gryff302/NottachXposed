package com.nottach.xposed.dialogs;

import com.nottach.xposed.R;
import com.nottach.xposed.R.string;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class CreditsDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		TextView tv = new TextView(getActivity());
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		tv.setText(R.string.credit_details);
		tv.setPadding(16, 16, 16, 16);
		builder.setCancelable(true).setView(tv).setTitle(R.string.credits)
				.setNegativeButton(R.string.awesome, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setPositiveButton(R.string.donate, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String url = "http://forum.xda-developers.com/donatetome.php?u=893892";
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(url));
						startActivity(i);
					}
				});
		return builder.create();
	}
}
