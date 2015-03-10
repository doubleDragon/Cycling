package com.android.cycling.widget;

import cn.bmob.im.db.BmobDB;

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

public class ContactListView extends AutoScrollListView implements
		OnClickListener {

	private BadgeView mNewFriendBadgeView;
	private TextView tv_new_name;
	private LinearLayout layout_new;
	private LinearLayout layout_near;

	private Context mContext;

	public ContactListView(Context context) {
		this(context, null);
	}

	public ContactListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ContactListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initViews();
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View headView = inflater
				.inflate(R.layout.bmob_include_new_friend, null);
		mNewFriendBadgeView = (BadgeView) headView
				.findViewById(R.id.new_friend_badge);
		layout_new = (LinearLayout) headView.findViewById(R.id.layout_new);
		layout_near = (LinearLayout) headView.findViewById(R.id.layout_near);
		layout_new.setOnClickListener(this);

		addHeaderView(headView);
	}

	public void updateNewInvite(boolean visible) {
		mNewFriendBadgeView.setTipVisible(visible);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
