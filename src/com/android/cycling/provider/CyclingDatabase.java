package com.android.cycling.provider;

import com.android.cycling.provider.CyclingConstants.Issue;
import com.android.cycling.provider.CyclingConstants.IssueColumns;
import com.android.cycling.provider.CyclingConstants.Photo;
import com.android.cycling.provider.CyclingConstants.PhotoColumns;

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
	}
	
	public interface Views {
		public static final String ISSUE = "view_issue";
	}

	public CyclingDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	private void createTriggers(SQLiteDatabase db) {
	}
	
	private void createViews(SQLiteDatabase db) {
		db.execSQL("DROP VIEW IF EXISTS " + Views.ISSUE + ";");
		
		String issueSelect = "SELECT "
				+ IssueColumns.CONCRETE_ID + " AS " + Issue._ID + ","
				+ Issue.NAME + ","
				+ Issue.LEVEL + ","
				+ Issue.PRICE + ","
				+ Issue.PHONE + ","
				+ Issue.TYPE + ","
				+ Issue.DESCRIPTION + ","
				+ PhotoColumns.CONCRETE_URI + " AS " + Issue.PHOTO
				+ " FROM " + Tables.ISSUE 
				+ " JOIN " + Tables.PHOTO + " ON("
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
				IssueColumns.DESCRIPTION + " TEXT NOT NULL" +
		");");
		db.execSQL("CREATE TABLE " + Tables.PHOTO + " (" +
				PhotoColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				PhotoColumns.ISSUE_ID + " INTEGER REFERENCES issue(_id)," +
				PhotoColumns.URI + " TEXT NOT NULL" +
		");");
		createViews(db);
		createTriggers(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
