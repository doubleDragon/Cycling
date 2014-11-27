package com.android.cycling.activities;

import com.android.cycling.R;
import com.android.cycling.pictures.SelectPicturesFragment;

import android.app.Activity;
import android.os.Bundle;

public class SelectPicturesActivity extends Activity {
	
	private SelectPicturesFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_pictures_activity);
		
		mFragment = (SelectPicturesFragment) getFragmentManager().
				findFragmentById(R.id.select_pictures_fragment);
	}
	
}
