package com.android.cycling.secondhand;

import com.android.cycling.R;
import com.android.cycling.activities.IssueEditorActivity;
import com.android.cycling.secondhand.IssueListLoader.Result;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class IssueListFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<IssueListLoader.Result>>{
	
	private static final int LOAD_ISSUES = 1000;
	
	private ImageView mPost;
	private TextView mEmptyView;
	private AutoScrollListView mListView;
	private IssueListAdapter mAdapter;
	
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
	}

	@Override
	public void onStart() {
		getLoaderManager().restartLoader(LOAD_ISSUES, null, this);
		super.onStart();
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
		
		mPost = (ImageView) rootView.findViewById(R.id.post);
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
	
	
}