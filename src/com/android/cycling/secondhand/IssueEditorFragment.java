package com.android.cycling.secondhand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.android.cycling.R;
import com.android.cycling.activities.IssueEditorActivity;
import com.android.cycling.activities.SelectPicturesActivity;
import com.android.cycling.widget.AddPhotoView;
import com.android.cycling.widget.SimpleGridView;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

public class IssueEditorFragment extends Fragment {
	
	private final String ADD_PHOTO_URI = "assets://addPhoto.png";
	
	private ContentResolver mContentResolver;
	private Context mContext;
	
	private String mAction;
	private Uri mUri;
	
	private ImageView mBack;
	private SimpleGridView mPhotoList;
	private PhotoListAdapter mAdapter;
	
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
		setEmptyData();
	}
	
	private void setEmptyData() {
		List<String> data = new ArrayList<String>();
		data.add(ADD_PHOTO_URI);
		mAdapter.setData(data);
	}
	
	public void setAdapterData(String[] uriPathArray) {
		if(uriPathArray == null || uriPathArray.length < 1) return;
		
		List<String> data = new ArrayList<String>(Arrays.asList(uriPathArray));
		if(data.size() < 6) {
			data.add(ADD_PHOTO_URI);
		}
		mAdapter.setData(data);
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
	public void onDetach() {
		mContext = null;
		super.onDetach();
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		View root = getView();
		if(root == null) {
			throw new IllegalStateException("Content view not yet created");
		}
		
		mBack = (ImageView) root.findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
			
		});
		
		mAdapter = new PhotoListAdapter(mContext);
		
		mPhotoList = (SimpleGridView) root.findViewById(R.id.pictureList);
		mPhotoList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int count = mAdapter.getCount();
				if(count == 1 || position == count) {
					intentToSelectPicture();
				} 
			}
			
		});
		mPhotoList.setAdapter(mAdapter);
	}
	
	private void intentToSelectPicture() {
		Intent i =  new Intent(mContext, SelectPicturesActivity.class);
		getActivity().startActivityForResult(i, IssueEditorActivity.REQUEST_SELECT_PICTURE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.issue_editor_fragment, container, false);
//		mRootView = inflater.inflate(R.layout.issue_editor_fragment, container, false);
		
//		mAddPhotoView = (AddPhotoView) mRootView.findViewById(R.id.photoContainer);
//		mAddPhotoView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent i =  new Intent(mContext, SelectPicturesActivity.class);
//				getActivity().startActivityForResult(i, IssueEditorActivity.REQUEST_SELECT_PICTURE);
//			}
//			
//		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	private void onRestoreInstanceState(Bundle state) {
		
	}
	
}
