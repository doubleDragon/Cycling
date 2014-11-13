package com.android.cycling.activities;

import com.android.cycling.R;
import com.android.cycling.secondhand.IssueEditorFragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class IssueEditorActivity extends Activity {
	
	private static final String TAG = IssueEditorActivity.class.getSimpleName();
	
	private IssueEditorFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String action = intent.getAction();
		
		setContentView(R.layout.issue_editor_activity);
		
		mFragment = (IssueEditorFragment) getFragmentManager().findFragmentById(R.id.issue_editor_fragment);
		mFragment.setContentResolver(getContentResolver());
		
		if(savedInstanceState == null) {
			Uri uri = Intent.ACTION_EDIT.equals(action) ? getIntent().getData() : null;
			mFragment.load(action, uri, getIntent().getExtras());
		}
	}
	
}
