package com.android.cycling.pictures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.android.cycling.R;
import com.android.cycling.pictures.SelectPicturesAdapter.PictureListItemViewCache;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectPicturesFragment extends Fragment implements SelectPicturesAdapter.Listener{
	
	//Append absolute path
	private static final String FILE_URI_PREFIX = "file://";
	
	private Context mContext;
	
	private GridView mPicureList;
	private TextView mChooseDis;
	private TextView mTotalCount;
	
	private SelectPicturesAdapter mAdapter;
	
	private HashSet<String> mCheckedUri = new HashSet<String>();
	private HashSet<Integer> mUnableCheckedPosition = new HashSet<Integer>();
	private boolean reachToMaxCount;
	
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
		mAdapter.setListener(this);
		mPicureList.setAdapter(mAdapter);

		//default 0 picture selected
		updateCheckedCount(0);
		
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
		
		mPicureList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(mUnableCheckedPosition.contains(position)) {
					//Toast user unable select
					toastToUser(R.string.picture_unable_select);
					return;
				}
				PictureListItemViewCache viewCache = (PictureListItemViewCache) view.getTag();
				if(viewCache != null) {
					changePictureCheckedStatus(viewCache.getPictureBean());
				}
			}
			
		});
	}

	@Override
	public void onActionUnableSelected(int position) {
		if(!mUnableCheckedPosition.contains(position)) {
			mUnableCheckedPosition.add(position);
		}
	}
	
	private void updateCheckedCount(int count) {
		mTotalCount.setText(getString(R.string.pictures_selected, count));
	}
	
	private void changePictureCheckedStatus(PictureBean bean) {
		if(bean == null) return;
		
		if(!bean.isSelected()){
			if(reachToMaxCount) {
				//Toast user max select picture count
				toastToUser(R.string.limit_picture_select_max_count);
				return;
			}
			mCheckedUri.add(bean.uri);
		} else {
			mCheckedUri.remove(bean.uri);
		}
		int afterSize = mCheckedUri.size();
		if(afterSize == 6) {
			reachToMaxCount = true;
		} else {
			reachToMaxCount = false;
		}
		updateCheckedCount(afterSize);
		
		bean.setSelected(!bean.isSelected());
		mAdapter.notifyDataSetChanged();
	}
	
	private void toastToUser(int resId) {
		Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
	}
	
	private class LoadTask extends AsyncTask<String, String, List<PictureBean>> {
		
		private ProgressDialog mDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog = new ProgressDialog(mContext);
			mDialog.show();
		}

		@Override
		protected List<PictureBean> doInBackground(String... params) {
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
			List<PictureBean> result = new ArrayList<PictureBean>();
			if(!pathSet.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for(String p : pathSet) {
					sb.setLength(0);
					sb.append(FILE_URI_PREFIX);
					sb.append(p);
					result.add(new PictureBean(sb.toString()));
				}
			}
			/*
			 * Test loadFailed
			 * result.add(new PictureBean(FILE_URI_PREFIX +
			 * "/sdcard/100000000.png"));
			 */
			return result;
		}

		@Override
		protected void onPostExecute(List<PictureBean> result) {
			super.onPostExecute(result);
			mDialog.dismiss();
			mAdapter.setData(result);
			mAdapter.notifyDataSetChanged();
		}
		
	}

}
