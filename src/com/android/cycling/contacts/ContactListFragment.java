package com.android.cycling.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;

import com.android.cycling.R;
import com.android.cycling.data.ServerUser;
import com.android.cycling.interactions.Event;
import com.android.cycling.util.CharacterParser;
import com.android.cycling.util.CollectionUtils;
import com.android.cycling.util.PinyinComparator;
import com.android.cycling.widget.ContactListView;
import com.android.cycling.widget.HeaderLayout;

import de.greenrobot.event.EventBus;

public class ContactListFragment extends Fragment {

	// index 3
	private static final String TAG = ContactListFragment.class.getSimpleName();

	private ContactListView mListView;
	private ContactListAdapter mAdapter;

	private Context mContext;

	private CharacterParser characterParser;

	private boolean mHasLoadOnce;

	private TagBroadcastReceiver mAddUserReceiver;
	private LoadTask mLoadTask;
	
	private void initTagMessageBroadCast(){
		mAddUserReceiver = new TagBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		intentFilter.setPriority(3);
		mContext.registerReceiver(mAddUserReceiver, intentFilter);
	}

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
		EventBus.getDefault().register(this);
		initTagMessageBroadCast();
		initData();
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		mContext.unregisterReceiver(mAddUserReceiver);
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		logW(TAG + "---------------------------onResume");
	}

	private void updateNewFriendTip(boolean visible) {
		logW(TAG + "---------------------------onStart");
		mListView.updateNewInvite(visible);
	}

	@Override
	public void onStart() {
		super.onStart();
		logW(TAG + "---------------------------onStart");
	}

	private void initData() {
		characterParser = CharacterParser.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.contact_list_fragment, container,
				false);
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
		// You can also assign the title programmatically by passing a
		// CharSequence or resource id.
		actionBar.setTitle(R.string.indicator_title_index3);

		mAdapter = new ContactListAdapter(mContext);
		mListView = (ContactListView) root.findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		setUserVisibleHint(true);
		super.onActivityCreated(savedInstanceState);
	}

	@SuppressLint("NewApi")
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser && !mHasLoadOnce) {
			queryMyfriends();
			mHasLoadOnce = true;
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	// @Override
	// public void onHiddenChanged(boolean hidden) {
	// super.onHiddenChanged(hidden);
	// mHidden = hidden;
	// if(!hidden){
	// refresh();
	// }
	// }

	private void queryMyfriends() {
		if (mLoadTask != null) {
			if (!mLoadTask.isCancelled()) {
				mLoadTask.cancel(true);
			}
		} else {
			mLoadTask = new LoadTask();
		}
		mLoadTask.execute();
	}

	private void filledData(List<BmobChatUser> datas, List<ServerUser> friends) {

		int total = datas.size();
		for (int i = 0; i < total; i++) {
			BmobChatUser user = datas.get(i);
			ServerUser sortModel = new ServerUser();
			sortModel.setAvatar(user.getAvatar());
			sortModel.setNick(user.getNick());
			sortModel.setUsername(user.getUsername());
			sortModel.setObjectId(user.getObjectId());
			sortModel.setContacts(user.getContacts());
			String username = sortModel.getUsername();
			if (username != null) {
				String pinyin = characterParser.getSelling(sortModel
						.getUsername());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
			} else {
				sortModel.setSortLetters("#");
			}
			friends.add(sortModel);
		}
		Collections.sort(friends, new PinyinComparator());

	}
	
	private void refreshInvite(BmobInvitation message) {
		updateNewFriendTip(true);
	}
	
	/**
	 * Eventbus callback,update listview header tip view
	 * @param event
	 */
	public void onEventMainThread(Event.AddContactRequestEvent event) {
		updateNewFriendTip(event.displayInviteTip);
	}

	private class LoadTask extends AsyncTask<Void, Void, List<ServerUser>> {

		@Override
		protected List<ServerUser> doInBackground(Void... params) {
			List<BmobChatUser> users = BmobDB.create(getActivity())
					.getContactList();
			if (users == null || users.isEmpty()) {
				return null;
			}
			Map<String, BmobChatUser> userMap = CollectionUtils.list2map(BmobDB
					.create(getActivity()).getContactList());
			List<ServerUser> friends = new ArrayList<ServerUser>();
			filledData(CollectionUtils.map2list(userMap), friends);
			users.clear();

			return friends;
		}

		@Override
		protected void onPostExecute(List<ServerUser> result) {
			super.onPostExecute(result);
			mAdapter.setData(result);
			mAdapter.notifyDataSetChanged();
		}

	}
	
	/**
	 * 经过测试，这个广播好像没用，也就是说不会收到好友请求消息,这里注释掉
	 * @author wsl
	 *
	 */
	private class TagBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
			logW("TagBroadcastReceiver invite message: " + message);
			refreshInvite(message);
			abortBroadcast();
		}
	}

	private static final boolean DEBUG = true;
	private static void logW(String msg) {
		if (DEBUG) {
			android.util.Log.d(TAG, msg);
		}
	}

}
