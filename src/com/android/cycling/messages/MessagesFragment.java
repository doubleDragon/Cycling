package com.android.cycling.messages;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

import com.android.cycling.R;
import com.android.cycling.widget.AutoScrollListView;
import com.android.cycling.widget.HeaderLayout;
import com.android.cycling.activities.ChatActivity;
import com.android.cycling.view.DialogTips;

public class MessagesFragment extends Fragment implements OnItemClickListener,
		OnItemLongClickListener {

	private static final String TAG = MessagesFragment.class.getSimpleName();

	private Context mContext;
	private AutoScrollListView mListView;
	private ConversationListAdapter mAdapter;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.message_conversation_fragment,
				container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		View root = getView();
		if (root == null) {
			throw new IllegalStateException("Content view not yet created");
		}

		HeaderLayout actionBar = (HeaderLayout) root
				.findViewById(R.id.header_layout);
		actionBar.setTitle(R.string.indicator_title_index1);

		mAdapter = new ConversationListAdapter(mContext,
				R.layout.conversation_list_item);
		mAdapter.setData(BmobDB.create(getActivity()).queryRecents());// ?loaded
																		// background?
		mListView = (AutoScrollListView) root.findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		BmobRecent recent = mAdapter.getItem(position);
		showDeleteDialog(recent);
		return true;
	}

	public void showDeleteDialog(final BmobRecent recent) {
		DialogTips dialog = new DialogTips(getActivity(), recent.getUserName(),
				"删除会话", "确定", true, true);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteRecent(recent);
			}
		});
		dialog.show();
		dialog = null;
	}

	private void deleteRecent(BmobRecent recent) {
		mAdapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
        BmobRecent recent = mAdapter.getItem(position);
        BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
        BmobChatUser user = new BmobChatUser();
        user.setAvatar(recent.getAvatar());
        user.setNick(recent.getNick());
        user.setUsername(recent.getUserName());
        user.setObjectId(recent.getTargetid());
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("user", user);
        mContext.startActivity(intent);

	}

}
