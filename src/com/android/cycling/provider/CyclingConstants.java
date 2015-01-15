package com.android.cycling.provider;

import com.android.cycling.provider.CyclingDatabase.Tables;

import android.net.Uri;
import android.provider.BaseColumns;

public final class CyclingConstants {
	
	public static final String AUTHORITY = "com.android.cycling";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
	
	public interface PhotoColumns {
		public static final String _ID = BaseColumns._ID;
		public static final String ISSUE_ID = "issue_id";
		public static final String URI = "uri";
		
		public static final String CONCRETE_ID = Tables.PHOTO + "." + _ID;
		public static final String CONCRETE_ISSUE_ID = Tables.PHOTO + "." + ISSUE_ID;
		public static final String CONCRETE_URI = Tables.PHOTO + "." + URI;
	}
	
	public static class Photo implements PhotoColumns{
		private Photo() {};
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "photo");
	}
	
	public interface IssueColumns {
		public static final String _ID = BaseColumns._ID;
		public static final String NAME = "name";
		public static final String TYPE = "type";//0,1,2
		public static final String LEVEL = "level";
		public static final String PHONE = "phone";
		public static final String PRICE = "price";
		public static final String DESCRIPTION = "description";
		public static final String DATE = "date";
		public static final String SERVER_ID = "server_id";
		
		public static final String CONCRETE_ID = Tables.ISSUE + "." + _ID;
	}
	
	public static class Issue implements IssueColumns {
		private Issue() {};
		
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "issues");
	}

}
