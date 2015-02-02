package com.android.cycling.secondhand;


import com.android.cycling.R;
import com.android.cycling.activities.IssueEditorActivity;
import com.android.cycling.secondhand.IssueListLoader.IssueResult;
import com.android.cycling.util.DataUtils;
import com.android.cycling.util.NetworkUtils;
import com.android.cycling.util.UserUtils;
import com.android.cycling.widget.AutoScrollListView;
import com.android.cycling.widget.HeaderLayout;
import com.android.cycling.widget.HeaderLayout.Action;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class IssueListFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<IssueListLoader.IssueResult>>{
	
	private static final int LOAD_ISSUES = 1000;
	
	private ImageButton mPost;
	private TextView mTitle;
	private TextView mEmptyView;
	private AutoScrollListView mListView;
	private IssueListAdapter mAdapter;
	
	private View mProgressContainer;
	
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
		logW(TAG + "-------------------onCreate");
		//Check the network,display toast
		if(NetworkUtils.isNetworkConnected(mContext)) {
			DataUtils.syncIssueFromServer(mContext);
		} else {
			toastMessage(R.string.no_network);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		logW(TAG + "-------------------onStart");
		getLoaderManager().restartLoader(LOAD_ISSUES, null, this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		logW(TAG + "-------------------onResume");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.issue_list_fragment, container, false);
		
		mProgressContainer = rootView.findViewById(R.id.progressContainer);
		
		mEmptyView = (TextView) rootView.findViewById(R.id.empty);
		
		mAdapter = new IssueListAdapter(mContext);
		
		mListView = (AutoScrollListView) rootView.findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setEmptyView(mEmptyView);
		
		HeaderLayout actionBar = (HeaderLayout) rootView.findViewById(R.id.header_layout);
		// You can also assign the title programmatically by passing a
		// CharSequence or resource id.
		actionBar.setTitle(R.string.indicator_title_index0);
		actionBar.addAction(new Action() {

			@Override
			public int getDrawable() {
				return R.drawable.sign_add_48;
			}

			@Override
			public void performAction(View view) {
				if(UserUtils.isNeedLogin(mContext)) {
					UserUtils.intentToLogin(mContext);
					return;
				}
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
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroy() {
		mListView = null;
        mEmptyView = null;
		super.onDestroy();
	}

	@Override
	public Loader<ArrayList<IssueResult>> onCreateLoader(int id, Bundle args) {
		mProgressContainer.setVisibility(View.VISIBLE);
		return new IssueListLoader(mContext);
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<IssueResult>> loader,
			ArrayList<IssueResult> data) {
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
        mProgressContainer.setVisibility(View.GONE);

	}

	@Override
	public void onLoaderReset(Loader<ArrayList<IssueResult>> loader) {
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
