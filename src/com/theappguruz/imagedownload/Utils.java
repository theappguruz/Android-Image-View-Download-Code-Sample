package com.theappguruz.imagedownload;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Utils {
	public static Boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static void showNetworkAlert(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("Please check your internet connection.")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

		builder.setTitle("Connection Problem");
		builder.show();
	}

	public static void displayMessage(String msg, Context mContext) {
		try {
			Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
		}
	}
}
