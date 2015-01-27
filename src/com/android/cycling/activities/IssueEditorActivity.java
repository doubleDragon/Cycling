package com.android.cycling.activities;


import com.android.cycling.CyclingActivity;
import com.android.cycling.R;
import com.android.cycling.secondhand.IssueEditorFragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

public class IssueEditorActivity extends CyclingActivity implements IssueEditorFragment.Listener{
	
	private static final String TAG = IssueEditorActivity.class.getSimpleName();
	
	public static final int REQUEST_SELECT_PICTURE = 1000;
	
	private IssueEditorFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String action = intent.getAction();
		
		setContentView(R.layout.issue_editor_activity);
		
		mFragment = (IssueEditorFragment) getFragmentManager().findFragmentById(R.id.issue_editor_fragment);
		mFragment.setListener(this);
		
		if(savedInstanceState == null) {
			Uri uri = Intent.ACTION_EDIT.equals(action) ? getIntent().getData() : null;
			mFragment.load(action, uri, getIntent().getExtras());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
		case REQUEST_SELECT_PICTURE:
			if(resultCode == Activity.RESULT_OK && data != null) {
				String[] uriPathArray = data.getStringArrayExtra(SelectPicturesActivity.EXTRA_PICTURE_URI);
				mFragment.setAdapterData(uriPathArray);
			}
			break;
		default:
			break;
		}
		
	}

	@Override
	public void start() {
		startDialog(R.string.sending);
	}

	@Override
	public void stop() {
		stopDialog();
	}
	
}
