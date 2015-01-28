package com.android.cycling.activities;


import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

import com.android.cycling.CyclingActivity;
import com.android.cycling.R;
import com.android.cycling.contacts.ContactDetailFragment;
import com.android.cycling.data.ServerUser;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class ContactDetailActivity extends CyclingActivity implements ContactDetailFragment.Listener{
	
	public static final String TAG = ContactDetailActivity.class.getSimpleName();
	public static final String EXTRA_SERVER_ID = "server_id";
	
	private ContactDetailFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_detail_activity);
		
		mFragment = (ContactDetailFragment) getFragmentManager()
				.findFragmentById(R.id.contact_editor_fragment);
		mFragment.setListener(this);
		
		String serverIdString = getIntent().getStringExtra(EXTRA_SERVER_ID);
		if(TextUtils.isEmpty(serverIdString)) {
			throw new NullPointerException("server id is null");
		}
		queryServerUserById(serverIdString);
	}
	
	private void queryServerUserById(final String id) {
		BmobQuery<ServerUser> query = new BmobQuery<ServerUser>();
		query.getObject(this, id, new GetListener<ServerUser>() {
			
			@Override
			public void onSuccess(ServerUser arg0) {
				mFragment.setServerUser(arg0);
				mFragment.notifyDataChanged();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				toastToUser("获取" + id + "失败");
				Log.d(TAG, "get server user id: " + id + "failed --- error: " + arg1);
				finish();
			}
		});

	}

	@Override
	public void onAddFriendStop(boolean success) {
		toastToUser("添加朋友" + success);
		stopDialog();
		finish();
	}

	@Override
	public void onAddFriendStart() {
		startDialog(R.string.sending);
	}

}
