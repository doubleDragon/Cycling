package com.android.cycling.provider;

import com.android.cycling.provider.CyclingConstants.Issue;
import com.android.cycling.provider.CyclingConstants.IssueColumns;
import com.android.cycling.provider.CyclingConstants.Photo;
import com.android.cycling.provider.CyclingConstants.PhotoColumns;
import com.android.cycling.provider.CyclingConstants.User;
import com.android.cycling.provider.CyclingConstants.UserColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CyclingDatabase extends SQLiteOpenHelper{
	
	private static final String TAG = CyclingDatabase.class.getSimpleName();
	
	private static final String DATABASE_NAME = "cycling.db";
	private static final int DATABASE_VERSION = 1;
	
	public interface Tables {
		public static final String ISSUE = "issue";
		public static final String PHOTO = "photo";
		public static final String USER = "user";
	}
	
	public interface Views {
		public static final String ISSUE = "view_issue";
	}

	public CyclingDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	private void createTriggers(SQLiteDatabase db) {
		db.execSQL("CREATE TRIGGER IF NOT EXISTS delete_photo_when_delete_issue " +
				"AFTER DELETE ON " + Tables.ISSUE + " " +
				"BEGIN " +
					"DELETE FROM " + Tables.PHOTO + " " +
					"WHERE " + PhotoColumns.ISSUE_ID + "=old._id;" +
				"END;");
	}
	
	private void createViews(SQLiteDatabase db) {
		db.execSQL("DROP VIEW IF EXISTS " + Views.ISSUE + ";");
		
		String userColumns = User.AVATAR + "," + User.USERNAME + ","
				+ User.EMAIL;
		
		String issueSelect = "SELECT "
				+ IssueColumns.CONCRETE_ID + " AS " + Issue._ID + ","
				+ Issue.NAME + ","
				+ Issue.LEVEL + ","
				+ Issue.PRICE + ","
				+ Issue.PHONE + ","
				+ Issue.TYPE + ","
				+ Issue.DESCRIPTION + ","
				+ Issue.DATE + ","
				+ IssueColumns.CONCRETE_SERVER_ID + " AS " + Issue.SERVER_ID + ","
				+ userColumns + ","
				+ Photo.URI //Photo table uri
				+ " FROM " + Tables.ISSUE
				+ " JOIN " + Tables.USER + " ON("
					+ Issue.USER_ID + "=" + UserColumns.CONCRETE_ID + ")" 
				+ " LEFT OUTER JOIN " + Tables.PHOTO + " ON("
					+ Issue.CONCRETE_ID + "=" + Photo.CONCRETE_ISSUE_ID + ")";
				
		db.execSQL("CREATE VIEW " + Views.ISSUE + " AS " + issueSelect);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Tables.ISSUE + " (" +
				IssueColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				IssueColumns.NAME + " TEXT NOT NULL," +
				IssueColumns.LEVEL + " TEXT NOT NULL," +
				IssueColumns.PHONE + " TEXT," +
				IssueColumns.PRICE + " TEXT NOT NULL," +
				IssueColumns.TYPE + " INTEGER NOT NULL DEFAULT 0," +
				IssueColumns.DESCRIPTION + " TEXT NOT NULL," +
				IssueColumns.DATE + " INTEGER NOT NULL," +
				IssueColumns.USER_ID + " INTEGER NOT NULL," +
				IssueColumns.SERVER_ID + " TEXT NOT NULL" +
		");");
		db.execSQL("CREATE TABLE " + Tables.PHOTO + " (" +
				PhotoColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				PhotoColumns.ISSUE_ID + " INTEGER REFERENCES issue(_id)," +
				PhotoColumns.URI + " TEXT NOT NULL" +
		");");
		db.execSQL("CREATE TABLE " + Tables.USER + " (" + 
				UserColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
				UserColumns.AVATAR + " TEXT," + 
				UserColumns.USERNAME + " TEXT NOT NULL," + 
				UserColumns.EMAIL + " TEXT NOT NULL," + 
				UserColumns.SERVER_ID + " TEXT UNIQUE NOT NULL" + 
		");");
		createViews(db);
		createTriggers(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
