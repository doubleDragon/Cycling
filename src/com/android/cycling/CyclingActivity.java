package com.android.cycling;

import android.app.ProgressDialog;

import com.android.cycling.activities.TransactionSafeActivity;

public class CyclingActivity extends TransactionSafeActivity {
	
	private ProgressDialog mDisplayDialog;
	
	protected void startDialog(int textResourceId) {
		if(mDisplayDialog != null) {
			mDisplayDialog.cancel();
			mDisplayDialog = null;
		}
		mDisplayDialog = new ProgressDialog(this);
		mDisplayDialog.setCancelable(false);
		mDisplayDialog.setCanceledOnTouchOutside(false);
		mDisplayDialog.setMessage(getResources().getString(textResourceId));
		mDisplayDialog.show();
	}
	
	protected void stopDialog() {
		if (mDisplayDialog != null) {
			mDisplayDialog.dismiss();
		}
	}
}
