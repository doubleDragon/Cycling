package com.android.cycling.secondhand;

import com.android.cycling.R;
import com.android.cycling.activities.SelectPicturesActivity;
import com.android.cycling.widget.AddPhotoView;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class IssueEditorFragment extends Fragment {
	
	private ContentResolver mContentResolver;
	private Context mContext;
	
	private String mAction;
	private Uri mUri;
	
	private View mRootView;
	
	private AddPhotoView mAddPhotoView;
	
	public void setContentResolver(ContentResolver contentResolver) {
		mContentResolver = contentResolver;
	}
	
	public void load(String action, Uri uri, Bundle extras) {
		mAction = action;
		mUri = uri;
	}
	
	private void startIssueDataLoader() {
		
	}
	
	private void setupEditor() {
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null) {
			onRestoreInstanceState(savedInstanceState);
		} else if(Intent.ACTION_EDIT.equals(mAction)) {
			//Load data from database base on mUri
			startIssueDataLoader();
		} else if(Intent.ACTION_INSERT.equals(mAction)) {
			setupEditor();
		} else {
			throw new IllegalArgumentException("Unknow Action String" + mAction +
					". Only support " + Intent.ACTION_EDIT + " or " + 
					Intent.ACTION_INSERT);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.issue_editor_fragment, container, false);
		mAddPhotoView = (AddPhotoView) mRootView.findViewById(R.id.photoContainer);
		mAddPhotoView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i =  new Intent(mContext, SelectPicturesActivity.class);
				mContext.startActivity(i);
			}
			
		});
		return mRootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	private void onRestoreInstanceState(Bundle state) {
		
	}
	
}
