package com.android.cycling.pictures;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.android.cycling.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

public class SelectPicturesFragment extends Fragment {
	
	//Append absolute path
	private static final String FILE_URI_PREFIX = "file://";
	
	private Context mContext;
	
	private GridView mPicureList;
	private TextView mChooseDis;
	private TextView mTotalCount;
	
	private SelectPicturesAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.select_pictures_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ensureViews();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mAdapter = new SelectPicturesAdapter(mContext);
		mPicureList.setAdapter(mAdapter);
		
		new LoadTask().execute();
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

	private void ensureViews() {
		View root = getView();
		if(root == null) {
			 throw new IllegalStateException("Content view not yet created");
		}
		mPicureList = (GridView) root.findViewById(R.id.pictureList);
		mChooseDis = (TextView) root.findViewById(R.id.chooseDir);
		mTotalCount = (TextView) root.findViewById(R.id.totalCount);
	}
	
	private static void log(String msg) {
		android.util.Log.d("test", msg);
	}
	
	private class LoadTask extends AsyncTask<String, String, List<String>> {
		
		private ProgressDialog mDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog = new ProgressDialog(mContext);
			mDialog.show();
		}

		@Override
		protected List<String> doInBackground(String... params) {
			HashSet<String> pathSet = new HashSet<String>();
			Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			ContentResolver mContentResolver = mContext.getContentResolver();
			Cursor c = mContentResolver.query(mImageUri, null,
					MediaStore.Images.Media.MIME_TYPE + "=? or "
							+ MediaStore.Images.Media.MIME_TYPE + "=?",
					new String[] { "image/jpeg", "image/png" },
					MediaStore.Images.Media.DATE_MODIFIED);
			try {
				c.moveToPosition(-1);
				while(c.moveToNext()) {
					String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
					pathSet.add(path);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(c != null) {
					c.close();
				}
			}
			List<String> result = new ArrayList<String>();
			if(!pathSet.isEmpty()) {
				log("result: " + pathSet.toString());
				StringBuilder sb = new StringBuilder();
				for(String p : pathSet) {
					sb.setLength(0);
					sb.append(FILE_URI_PREFIX);
					sb.append(p);
					result.add(sb.toString());
					log("path: " + p + " ---uri: " + sb.toString());
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			mDialog.dismiss();
			mAdapter.setData(result);
			mAdapter.notifyDataSetChanged();
		}
		
	}
	
}
