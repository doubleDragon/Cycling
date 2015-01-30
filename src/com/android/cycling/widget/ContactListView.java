package com.android.cycling.widget;

import com.android.cycling.R;
import com.android.cycling.activities.NewFriendActivity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class ContactListView extends AutoScrollListView implements OnClickListener{
	
	private ImageView iv_msg_tips;
	private TextView tv_new_name;
	private LinearLayout layout_new;
	private LinearLayout layout_near;

	public ContactListView(Context context) {
		this(context, null);
	}

	public ContactListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ContactListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initViews();
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View headView = inflater.inflate(R.layout.bmob_include_new_friend, null);
		iv_msg_tips = (ImageView)headView.findViewById(R.id.iv_msg_tips);
		layout_new =(LinearLayout)headView.findViewById(R.id.layout_new);
		layout_near =(LinearLayout)headView.findViewById(R.id.layout_near);
		
		layout_new.setOnClickListener(this);
		
		addHeaderView(headView);
	}
	
	public void updateNewInvite() {
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.layout_new:
			intentToNewFriendActivity();
			break;
		default:
			break;
		}
	}
	
	private void intentToNewFriendActivity() {
		Intent intent = new Intent(getContext(), NewFriendActivity.class);
		intent.putExtra("from", "contact");
		getContext().startActivity(intent);
	}

}
