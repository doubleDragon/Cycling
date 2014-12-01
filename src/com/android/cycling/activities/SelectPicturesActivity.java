package com.android.cycling.activities;

import com.android.cycling.R;
import com.android.cycling.pictures.SelectPicturesFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SelectPicturesActivity extends Activity implements SelectPicturesFragment.Listener{
	
	public static final String EXTRA_PICTURE_URI = "extra_picture_uri";
	
	private SelectPicturesFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_pictures_activity);
		
		mFragment = (SelectPicturesFragment) getFragmentManager().
				findFragmentById(R.id.select_pictures_fragment);
		mFragment.setListener(this);
	}

	@Override
	public void onActionCancel() {
		setResult(Activity.RESULT_CANCELED);
		finish();
	}

	@Override
	public void onActionConfirm(String[] uriSet) {
		Intent data = new Intent();
		data.putExtra(EXTRA_PICTURE_URI, uriSet);
		setResult(Activity.RESULT_OK, data);
		finish();
	}

}
