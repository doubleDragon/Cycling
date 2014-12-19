package com.android.cycling.provider;

import com.android.cycling.provider.CyclingConstants.Issue;
import com.android.cycling.provider.CyclingConstants.Photo;
import com.android.cycling.provider.CyclingDatabase.Tables;
import com.android.cycling.provider.CyclingDatabase.Views;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class CyclingProvider extends ContentProvider{
	
	private static final UriMatcher sUriMatcher;
	private static final int ISSUES = 1;
	private static final int ISSUES_ID = 2;
	private static final int PHOTO = 3;
	private static final int PHOTO_ID = 4;
	
	private static final ProjectionMap sIssuesProjectionMap = ProjectionMap.builder()
            .add(Issue._ID)
            .add(Issue.NAME)
            .add(Issue.TYPE)
            .add(Issue.LEVEL)
            .add(Issue.PHONE)
            .add(Issue.PRICE)
            .add(Issue.DESCRIPTION)
            .add(Issue.PHOTO, Tables.PHOTO + "." + Photo.URI)
            .build();
	
	private static final ProjectionMap sPhotoProjectionMap = ProjectionMap.builder()
			.add(Photo._ID)
			.add(Photo.URI)
			.build();
	
	private CyclingDatabase mOpenHelper;
	
	static {
		sUriMatcher =  new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(CyclingConstants.AUTHORITY, "issues", ISSUES);
		sUriMatcher.addURI(CyclingConstants.AUTHORITY, "issues/#", ISSUES_ID);
		sUriMatcher.addURI(CyclingConstants.AUTHORITY, "photo", PHOTO);
		sUriMatcher.addURI(CyclingConstants.AUTHORITY, "photo/#", PHOTO_ID);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new CyclingDatabase(getContext());
		return true;
	}
	
	private void setTablesAndProjectionMapForIssue(SQLiteQueryBuilder qb) {
		StringBuilder sb = new StringBuilder();
        sb.append(Views.ISSUE);
        sb.append(" issue");
        qb.setTables(sb.toString());
        qb.setDistinct(true);
        qb.setProjectionMap(sIssuesProjectionMap);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String groupBy = null;
        String having = null;
//        String limit = null;
		
		final int match = sUriMatcher.match(uri);
		switch(match) {
		case ISSUES:
			setTablesAndProjectionMapForIssue(qb);
			break;
		case ISSUES_ID:
			long issueId = ContentUris.parseId(uri);
			setTablesAndProjectionMapForIssue(qb);
			selectionArgs = insertSelectionArg(selectionArgs, String.valueOf(issueId));
			qb.appendWhere(Issue._ID + "=?");
		case PHOTO:
			qb.setTables(Tables.PHOTO);
			qb.setProjectionMap(sPhotoProjectionMap);
			break;
		case PHOTO_ID:
			long photoId = ContentUris.parseId(uri);
			qb.setTables(Tables.PHOTO);
			qb.setProjectionMap(sPhotoProjectionMap);
			selectionArgs = insertSelectionArg(selectionArgs, String.valueOf(photoId));
			qb.appendWhere(Photo._ID + "=?");
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		qb.setStrict(true);
		
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
		if (c != null) {
            c.setNotificationUri(getContext().getContentResolver(), CyclingConstants.AUTHORITY_URI);
        }
		return c;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}
	
	private long insertIssue(SQLiteDatabase db, ContentValues values) {
		long issueId = db.insert(Tables.ISSUE, Issue._ID, values);
		return issueId;
	}
	
	private long insertPhoto(SQLiteDatabase db, ContentValues values) {
		long photoId = db.insert(Tables.PHOTO, Photo._ID, values);
		return photoId;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		long id = 0;
		switch(match) {
		case ISSUES:
			id = insertIssue(db, values);
			break;
		case PHOTO:
			id = insertPhoto(db, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		if(id < 0) {
			return null;
		}
		notifyChange(match);
		return ContentUris.withAppendedId(uri, id);
	}
	
	private void notifyChange(int match) {
		Uri uri;
		switch(match) {
		case ISSUES:
			uri = Issue.CONTENT_URI;
			break;
		default:
			uri = CyclingConstants.AUTHORITY_URI;
			break;
		}
		getContext().getContentResolver().notifyChange(uri, null);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
	
	/**
     * Inserts an argument at the beginning of the selection arg list.
     */
    private String[] insertSelectionArg(String[] selectionArgs, String arg) {
        if (selectionArgs == null) {
            return new String[] {arg};
        } else {
            int newLength = selectionArgs.length + 1;
            String[] newSelectionArgs = new String[newLength];
            newSelectionArgs[0] = arg;
            System.arraycopy(selectionArgs, 0, newSelectionArgs, 1, selectionArgs.length);
            return newSelectionArgs;
        }
    }

}
