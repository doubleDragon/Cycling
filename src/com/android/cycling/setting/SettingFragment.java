package com.android.cycling.setting;

import cn.bmob.v3.BmobUser;

import com.android.cycling.R;
import com.android.cycling.activities.LoginActivity;
import com.android.cycling.data.ServerUser;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class SettingFragment extends Fragment{

	private static final String TAG = SettingFragment.class.getSimpleName();
	
	
	private Context mContext;
	private ServerUser mSelfUser;
	private boolean isNeedLogin;
	private boolean isNeedSwitch;
	
	private SettingContentFragment mContentFragment;
	private UnavailableFragment mUnavailableFragment;
	
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
		if(savedInstanceState == null) {
			isNeedSwitch = true;
		} else {
			isNeedSwitch = false;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mSelfUser = BmobUser.getCurrentUser(mContext, ServerUser.class);
		if(mSelfUser == null) {
			isNeedLogin = true;
		} else {
			isNeedLogin = false;
		}
		displayFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.setting_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	private void displayFragment() {
		if(!isNeedSwitch) {
			return;
		}
		
		FragmentManager fm = getFragmentManager();
		
		FragmentTransaction transaction = fm.beginTransaction();
		mUnavailableFragment = (UnavailableFragment)fm.findFragmentByTag("unavail_fragment");
		mContentFragment = (SettingContentFragment)fm.findFragmentByTag("content_fragment");
		
		if(mUnavailableFragment == null) {
			mUnavailableFragment = new UnavailableFragment();
			transaction.add(R.id.content_frame, mUnavailableFragment, "unavail_fragment");
		}
		if(mContentFragment == null) {
			mContentFragment = new SettingContentFragment();
			transaction.add(R.id.content_frame, mContentFragment, "content_fragment");
		}
		mContentFragment.setSelfUser(mSelfUser);
		
		transaction.hide(mUnavailableFragment);
		transaction.hide(mContentFragment);
		
		
		if(!isNeedLogin) {
			transaction.show(mContentFragment);
		} else {
			transaction.show(mUnavailableFragment);
		}
		transaction.commit();
	}
	
	private static class UnavailableFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Button text = new Button(getActivity());
			text.setGravity(Gravity.CENTER);
			text.setText(R.string.login);
			text.setTextSize(20 * getResources().getDisplayMetrics().density);
			text.setPadding(20, 20, 20, 20);
			text.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i =  new Intent(getActivity(), LoginActivity.class);
					getActivity().startActivity(i);
				}
				
			});

			LinearLayout layout = new LinearLayout(getActivity());
			layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			layout.setGravity(Gravity.CENTER);
			layout.addView(text);

			return layout;
		}
	}

}
