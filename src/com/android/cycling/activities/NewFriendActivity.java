package com.android.cycling.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

import com.android.cycling.R;
import com.android.cycling.contacts.NewFriendAdapter;

public class NewFriendActivity extends Activity implements OnItemClickListener, OnItemLongClickListener{
	
	private ListView listview;
	
	private NewFriendAdapter adapter;
	
	String from="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_friend_activity);
		from = getIntent().getStringExtra("from");
		initView();
	}
	
	private void initView(){
		List<BmobInvitation> data = BmobDB.create(this).queryBmobInviteList();
		adapter = new NewFriendAdapter(this);
		adapter.setData(data);
	
		TextView emptyView = (TextView) findViewById(R.id.empty);
		
		listview = (ListView)findViewById(R.id.list_newfriend);
		listview.setOnItemLongClickListener(this);
		listview.setEmptyView(emptyView);
		
		listview.setAdapter(adapter);
		if (from == null) {
			listview.setSelection(adapter.getCount());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
