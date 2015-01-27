package com.android.cycling.activities;


import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

import com.android.cycling.R;
import com.android.cycling.contacts.ContactEditorFragment;
import com.android.cycling.data.ServerUser;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class ContactEditorActivity extends Activity {
	
	public static final String TAG = ContactEditorActivity.class.getSimpleName();
	public static final String EXTRA_SERVER_ID = "server_id";
	
	private ContactEditorFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_editor_activity);
		
		mFragment = (ContactEditorFragment) getFragmentManager()
				.findFragmentById(R.id.contact_editor_fragment);
		
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
				// TODO Auto-generated method stub
				mFragment.setServerUser(arg0);
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(ContactEditorActivity.this, "获取" + id + "失败", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "get server user id: " + id + "failed --- error: " + arg1);
				finish();
			}
		});

	}

}
