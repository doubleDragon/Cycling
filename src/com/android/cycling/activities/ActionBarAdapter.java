package com.android.cycling.activities;

public class ActionBarAdapter {

	
	public interface TabState {
		public static int SECOND_HAND = 0;
		public static int MESSAGES = 1;
		public static int AROUND = 2;
		public static int CONTACTS = 3;
		public static int SETTINGS = 4;
		
		public static int COUNT  = 5;
		public static int DEFAULT = TabState.SECOND_HAND;
	}
}
