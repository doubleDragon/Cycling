package com.android.cycling.interactions;

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

}
