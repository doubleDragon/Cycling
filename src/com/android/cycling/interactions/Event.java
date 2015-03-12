package com.android.cycling.interactions;

import cn.bmob.im.bean.BmobMsg;

public class Event {

	public static class AddContactRequestEvent {

		public final boolean displayInviteTip;

		public AddContactRequestEvent(boolean displayInviteTip) {
			super();
			this.displayInviteTip = displayInviteTip;
		}
	}

	public static class AddContactAgreeEvent {

	}

	public static class PullListViewEvent {
	}

	public static class MessageEvent {
		public final BmobMsg msg;

		public MessageEvent(BmobMsg msg) {
			this.msg = msg;
		}
	}

	public static class ReadedEvent {
		public final String conversionId;
		public final String msgTime;

		public ReadedEvent(String conversionId, String msgTime) {
			this.conversionId = conversionId;
			this.msgTime = msgTime;
		}

	}

}
