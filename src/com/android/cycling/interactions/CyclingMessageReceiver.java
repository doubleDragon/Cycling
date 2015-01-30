package com.android.cycling.interactions;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.android.cycling.CyclingApplication;
import com.android.cycling.R;
import com.android.cycling.activities.MainActivity;
import com.android.cycling.activities.NewFriendActivity;
import com.android.cycling.util.NetworkUtils;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class CyclingMessageReceiver extends BroadcastReceiver {

	// 如果你想发送自定义格式的消息，请使用sendJsonMessage方法来发送Json格式的字符串，然后你按照格式自己解析并处理

	public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();

	public static final int NOTIFY_ID = 0x000;
	public static int mNewNum = 0;//
	BmobUserManager mUserManager;
	BmobChatUser mCurrentUser;

	@Override
	public void onReceive(Context context, Intent intent) {
		String json = intent.getStringExtra("msg");
		BmobLog.i("收到的message = " + json);
		mUserManager = BmobUserManager.getInstance(context);
		mCurrentUser = mUserManager.getCurrentUser();
		boolean isNetConnected = NetworkUtils.isNetworkConnected(context);
		if (isNetConnected) {
			parseMessage(context, json);
		} else {
			for (int i = 0; i < ehList.size(); i++)
				((EventListener) ehList.get(i)).onNetChange(isNetConnected);
		}
	}
	
	/**
	 * 保存好友请求道本地，并更新后台的未读字段
	 * @param context
	 * @param json
	 * @param toId
	 */
	private void tagToAddContact(Context context, String json, String toId) {
		BmobInvitation message = BmobChatManager.getInstance(context)
				.saveReceiveInvite(json, toId);
		if (mCurrentUser != null) {
			if (toId.equals(mCurrentUser.getObjectId())) {
				if (ehList.size() > 0) {
					for (EventListener handler : ehList)
						handler.onAddUser(message);
				} else {
					showOtherNotify(context, message.getFromname(), toId,
							message.getFromname() + "请求添加好友",
							NewFriendActivity.class);
				}
			}
		}
	}
	
	/**
	 * 收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
	 * @param context
	 * @param jo
	 * @param toId
	 * @param json
	 */
	private void tagToAddAgree(Context context, JSONObject jo, String toId,
			String json) {
		String username = BmobJsonUtil.getString(jo,
				BmobConstant.PUSH_KEY_TARGETUSERNAME);
		BmobUserManager.getInstance(context).addContactAfterAgree(username,
				new FindListener<BmobChatUser>() {

					@Override
					public void onError(int arg0, final String arg1) {
						// TODO Auto-generated

					}

					@Override
					public void onSuccess(List<BmobChatUser> arg0) {
						// CyclingApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));
						//Save contact list to local db
					}
				});
		showOtherNotify(context, username, toId, username + "同意添加您为好友",
				MainActivity.class);
		BmobMsg.createAndSaveRecentAfterAgree(context, json);
	}
	
	/**
	 * 已读回执
	 * @param context
	 * @param jo
	 * @param msgTime
	 * @param toId
	 */
	private void toToReaded(Context context, JSONObject jo, String msgTime, String toId) {
		String conversionId = BmobJsonUtil.getString(jo,
				BmobConstant.PUSH_READED_CONVERSIONID);
		if (mCurrentUser != null) {
			BmobChatManager.getInstance(context)
					.updateMsgStatus(conversionId, msgTime);
			if (toId.equals(mCurrentUser.getObjectId())) {
				if (ehList.size() > 0) {
					for (EventListener handler : ehList)
						handler.onReaded(conversionId, msgTime);
				}
			}
		}
	}

	private void parseMessage(final Context context, String json) {
		JSONObject jo;
		try {
			jo = new JSONObject(json);
			String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
			if (tag.equals(BmobConfig.TAG_OFFLINE)) {
				if (mCurrentUser != null) {
					if (ehList.size() > 0) {
						for (EventListener handler : ehList)
							handler.onOffline();
					} else {
						CyclingApplication.getInstance().logout();
					}
				}
			} else {
				String fromId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TARGETID);
				final String toId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TOID);
				String msgTime = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_READED_MSGTIME);
				if (fromId != null
						&& !BmobDB.create(context, toId).isBlackUser(fromId)) {
					if (TextUtils.isEmpty(tag)) {
						// 不携带tag标签--此可接收陌生人的消息
						BmobChatManager.getInstance(context).createReceiveMsg(
								json, new MyOnReceiveListener(context, toId));
					} else if (tag.equals(BmobConfig.TAG_ADD_CONTACT)) {
						tagToAddContact(context, json, toId);
					} else if (tag.equals(BmobConfig.TAG_ADD_AGREE)) {
						tagToAddAgree(context, jo, toId, json);
					} else if (tag.equals(BmobConfig.TAG_READED)) {
						toToReaded(context, jo, msgTime, toId);
					}
				} else {
					BmobChatManager.getInstance(context).updateMsgReaded(true,
							fromId, msgTime);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showMsgNotify(Context context, BmobMsg msg) {
		int icon = R.drawable.ic_launcher;
		String trueMsg = "";
		if (msg.getMsgType() == BmobConfig.TYPE_TEXT
				&& msg.getContent().contains("\\ue")) {
			trueMsg = "[表情]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
			trueMsg = "[图片]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
			trueMsg = "[语音]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
			trueMsg = "[位置]";
		} else {
			trueMsg = msg.getContent();
		}
		CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
		String contentTitle = msg.getBelongUsername() + " (" + mNewNum
				+ "条新消息)";

		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		boolean isAllowVoice = CyclingApplication.getInstance().getSpUtil()
				.isAllowVoice();
		boolean isAllowVibrate = CyclingApplication.getInstance().getSpUtil()
				.isAllowVibrate();

		BmobNotifyManager.getInstance(context).showNotifyWithExtras(
				isAllowVoice, isAllowVibrate, icon, tickerText.toString(),
				contentTitle, tickerText.toString(), intent);
	}

	public void showOtherNotify(Context context, String username, String toId,
			String ticker, Class<?> cls) {
		boolean isAllow = CyclingApplication.getInstance().getSpUtil()
				.isAllowPushNotify();
		boolean isAllowVoice = CyclingApplication.getInstance().getSpUtil()
				.isAllowVoice();
		boolean isAllowVibrate = CyclingApplication.getInstance().getSpUtil()
				.isAllowVibrate();
		if (isAllow && mCurrentUser != null
				&& mCurrentUser.getObjectId().equals(toId)) {
			BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,
					isAllowVibrate, R.drawable.ic_launcher, ticker, username,
					ticker.toString(), NewFriendActivity.class);
		}
	}

	private class MyOnReceiveListener implements OnReceiveListener {

		private Context context;
		private String toId;

		public MyOnReceiveListener(Context context, String toId) {
			this.context = context;
			this.toId = toId;
		}

		@Override
		public void onFailure(int arg0, String arg1) {
			BmobLog.i("获取接收的消息失败：" + arg1);
		}

		@Override
		public void onSuccess(BmobMsg msg) {
			if (ehList.size() > 0) {
				for (int i = 0; i < ehList.size(); i++) {
					((EventListener) ehList.get(i)).onMessage(msg);
				}
			} else {
				boolean isAllow = CyclingApplication.getInstance().getSpUtil()
						.isAllowPushNotify();
				if (isAllow && mCurrentUser != null
						&& mCurrentUser.getObjectId().equals(toId)) {
					mNewNum++;
					showMsgNotify(context, msg);
				}
			}
		}

	};

}
