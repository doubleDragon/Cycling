package com.android.cycling.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;

import com.android.cycling.R;
import com.android.cycling.data.ServerUser;
import com.android.cycling.util.CharacterParser;
import com.android.cycling.util.CollectionUtils;
import com.android.cycling.util.PinyinComparator;
import com.android.cycling.widget.ContactListView;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ContactListFragment extends Fragment {
	
	//index 3
	private static final String TAG = ContactListFragment.class.getSimpleName();
	
	private ContactListView mListView;
	private ContactListAdapter mAdapter;
	
	private Context mContext;
	
	private CharacterParser characterParser;
	
//	private boolean mHidden;
	
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
		initData();
		queryMyfriends();
	}
	
	private void initData() {
		characterParser = CharacterParser.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.contact_list_fragment, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		View root = getView();
		if(root == null) {
			throw new IllegalStateException("Content view not yet created");
		}
		
		mAdapter = new ContactListAdapter(mContext);
		mListView = (ContactListView) root.findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
//	@Override
//	public void setUserVisibleHint(boolean isVisibleToUser) {
//		logW("setUserVisibleHint ---isVisibleToUser :" + isVisibleToUser);
//		if (isVisibleToUser) {
//			queryMyfriends();
//		}
//		super.setUserVisibleHint(isVisibleToUser);
//	}
	
//	@Override
//	public void onResume() {
//		super.onResume();
//		logW(TAG + "---------------------------onResume");
//		if(!mHidden) {
//			queryMyfriends();
//		}
//	}
	
	
//	@Override
//	public void onHiddenChanged(boolean hidden) {
//		super.onHiddenChanged(hidden);
//		mHidden = hidden;
//		if(!hidden){
//			refresh();
//		}
//	}

//	public void refresh() {
//		try {
//			getActivity().runOnUiThread(new Runnable() {
//				public void run() {
//					queryMyfriends();
//				}
//			});
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	private void queryMyfriends() {
		new LoadTask().execute();
//		List<BmobChatUser> users = BmobDB.create(getActivity()).getContactList();
//		if(users == null || users.isEmpty()) return;
//		Map<String, BmobChatUser> userMap = CollectionUtils.list2map(BmobDB.create(getActivity()).getContactList());
//		List<ServerUser> friends = new ArrayList<ServerUser>();
//		filledData(CollectionUtils.map2list(userMap), friends);
//		users.clear();
//		
//		mAdapter.setData(friends);
//		mAdapter.notifyDataSetChanged();
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
				String pinyin = characterParser.getSelling(sortModel.getUsername());
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
	
	private class LoadTask extends AsyncTask<Void, Void, List<ServerUser>> {

		@Override
		protected List<ServerUser> doInBackground(Void... params) {
			List<BmobChatUser> users = BmobDB.create(getActivity()).getContactList();
			if(users == null || users.isEmpty()) {
				return null;
			}
			Map<String, BmobChatUser> userMap = CollectionUtils.list2map(BmobDB.create(getActivity()).getContactList());
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
	
	private static final boolean DEBUG = true;
    private static void logW(String msg) {
    	if(DEBUG) {
    		android.util.Log.d(TAG, msg);
    	}
    }
	
}
