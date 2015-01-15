package com.android.cycling.secondhand;

import cn.volley.Network;

import com.android.cycling.CycingSaveService;
import com.android.cycling.R;
import com.android.cycling.activities.IssueEditorActivity;
import com.android.cycling.secondhand.IssueListLoader.Result;
import com.android.cycling.util.NetworkUtils;
import com.android.cycling.widget.AutoScrollListView;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class IssueListFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<IssueListLoader.Result>>{
	
	private static final int LOAD_ISSUES = 1000;
	
	private ImageButton mPost;
	private TextView mTitle;
	private TextView mEmptyView;
	private AutoScrollListView mListView;
	private IssueListAdapter mAdapter;
	
	private PopupWindow mDisplayType;
	
	private Context mContext;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		logW("onCreate");
		
		//Check the network,display toast
		if(NetworkUtils.isNetworkConnected(mContext)) {
			syncIssueFromServer();
		} else {
			toastMessage(R.string.no_network);
		}
	}
	
	private void syncIssueFromServer() {
		Intent i = CycingSaveService.createSyncIssueIntent(mContext);
		mContext.startService(i);
	}

	@Override
	public void onStart() {
		super.onStart();
		logW("onStart");
		getLoaderManager().restartLoader(LOAD_ISSUES, null, this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		logW("onResume");
	}

	private void showIssueType() {
		if(mDisplayType == null) {
			mDisplayType = new PopupWindow(mContext);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.secondhand_fragment, container, false);
		
		mEmptyView = (TextView) rootView.findViewById(R.id.empty);
		
		mAdapter = new IssueListAdapter(mContext);
		
		mListView = (AutoScrollListView) rootView.findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setEmptyView(mEmptyView);
		
		mTitle = (TextView) rootView.findViewById(R.id.title);
		mTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//showIssueType();
			}
			
		});
		mPost = (ImageButton) rootView.findViewById(R.id.post);
		mPost.setImageResource(R.drawable.sign_add_48);
		mPost.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Check login state
				Intent i = new Intent(getActivity(), IssueEditorActivity.class);
				i.setAction(Intent.ACTION_INSERT);
				startActivity(i);
			}
		});
		
		return rootView;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		logW("onPause");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		logW("onStop");
	}

	@Override
	public void onDestroy() {
		logW("onDestroy");
		mListView = null;
        mEmptyView = null;
		super.onDestroy();
	}

	@Override
	public Loader<ArrayList<Result>> onCreateLoader(int id, Bundle args) {
		return new IssueListLoader(mContext);
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<Result>> loader,
			ArrayList<Result> data) {
		if (mAdapter != null) {
            mAdapter.notifyDataSetInvalidated();
        }

        mAdapter.setData(data);
        // The list should now be shown.
//        if (isResumed()) {
//            setListShown(true);
//        } else {
//            setListShownNoAnimation(true);
//        }

        // Hide the progress indicator
//        mProgressContainer.setVisibility(View.GONE);

	}

	@Override
	public void onLoaderReset(Loader<ArrayList<Result>> loader) {
		mAdapter.setData(null);
	}
	
	private void toastMessage(int msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
	
	private static final boolean DEBUG = true;
	private static final String TAG = IssueListFragment.class.getSimpleName();
	private static void logW(String msg) {
		if(DEBUG) {
		android.util.Log.d(TAG, msg);
		}
	}
	
}
