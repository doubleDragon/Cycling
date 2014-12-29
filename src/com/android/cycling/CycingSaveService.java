package com.android.cycling;

import java.util.ArrayList;

import com.android.cycling.provider.CyclingConstants;
import com.android.cycling.provider.CyclingConstants.Issue;
import com.android.cycling.provider.CyclingConstants.Photo;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

public class CycingSaveService extends IntentService{
	
	private static final String TAG = CycingSaveService.class.getSimpleName();
	
	private static final String ACTION_SAVE_ISSUE = "saveIssue";
	private static final String EXTRA_ISSUE_NAME = "issueName";
	private static final String EXTRA_ISSUE_LEVEL = "issueLevel";
	private static final String EXTRA_ISSUE_PRICE = "issuePrice";
	private static final String EXTRA_ISSUE_PHONE = "issuePhone";
	private static final String EXTRA_ISSUE_PHOTO = "issuePhoto";
	private static final String EXTRA_ISSUE_TYPE = "issueType";
	private static final String EXTRA_ISSUE_DESCRIPTION = "issueDescription";
	private static final String EXTRA_ISSUE_DATE = "date";

	public CycingSaveService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final String action = intent.getAction();
		if(action.equals(ACTION_SAVE_ISSUE)) {
			saveIssue(intent);
		}
	}
	
	private void saveIssue(Intent intent) {
		String name = intent.getStringExtra(EXTRA_ISSUE_NAME);
		String level = intent.getStringExtra(EXTRA_ISSUE_LEVEL);
		String price = intent.getStringExtra(EXTRA_ISSUE_PRICE);
		String phone = intent.getStringExtra(EXTRA_ISSUE_PHONE);
		String description = intent.getStringExtra(EXTRA_ISSUE_DESCRIPTION);
		long date = intent.getLongExtra(EXTRA_ISSUE_DATE, -1);
		if(date == -1) {
			date = System.currentTimeMillis();
		}
		int type = intent.getIntExtra(EXTRA_ISSUE_TYPE, 0);
		String[] pictures = intent.getStringArrayExtra(EXTRA_ISSUE_PHOTO);
		
		if(pictures != null && pictures.length > 0) {
			ArrayList<ContentProviderOperation> operations = new  ArrayList<ContentProviderOperation>();
			ContentProviderOperation op;
			op = ContentProviderOperation.newInsert(Issue.CONTENT_URI)
					.withValue(Issue.NAME, name)
					.withValue(Issue.LEVEL, level)
					.withValue(Issue.PRICE, price)
					.withValue(Issue.PHONE, phone)
					.withValue(Issue.TYPE, type)
					.withValue(Issue.DESCRIPTION, description)
					.withValue(Issue.DATE, date)
					.build();
			operations.add(op);
			for(String pic : pictures) {
				op = ContentProviderOperation.newInsert(Photo.CONTENT_URI)
						.withValueBackReference(Photo.ISSUE_ID, 0)
						.withValue(Photo.URI, pic)
						.build();
				operations.add(op);
			}
			try {
				ContentProviderResult[] results = getContentResolver()
						.applyBatch(CyclingConstants.AUTHORITY, operations);
				for (ContentProviderResult result : results) {
	                Log.i(TAG, result.uri.toString());
	            }
			} catch (RemoteException e) {
				Log.e(TAG, "Problem persisting user edits", e);
			} catch (OperationApplicationException e) {
				Log.e(TAG, "Insert fails", e);
			}
			operations.clear();
		} else {
			ContentValues values = new ContentValues();
			values.put(Issue.NAME, name);
			values.put(Issue.LEVEL, level);
			values.put(Issue.PRICE, price);
			values.put(Issue.PHONE, phone);
			values.put(Issue.TYPE, type);
			values.put(Issue.DESCRIPTION, description);
			values.put(Issue.DATE, date);
			getContentResolver().insert(Issue.CONTENT_URI, values);
		}
	}
	
	public static Intent createSaveIssueIntent(Context context, String name, 
			String level, String price, String description, long date, String phone, 
			int type, String[] pictures) {
		Intent i = new Intent(context, CycingSaveService.class);
		i.setAction(ACTION_SAVE_ISSUE);
		i.putExtra(EXTRA_ISSUE_NAME, name);
		i.putExtra(EXTRA_ISSUE_LEVEL, level);
		i.putExtra(EXTRA_ISSUE_PRICE, price);
		i.putExtra(EXTRA_ISSUE_PHONE, phone);
		i.putExtra(EXTRA_ISSUE_TYPE, type);
		i.putExtra(EXTRA_ISSUE_DESCRIPTION, description);
		i.putExtra(EXTRA_ISSUE_DATE, date);
		if(pictures != null && pictures.length >= 0) {
			i.putExtra(EXTRA_ISSUE_PHOTO, pictures);
		}
		return i;
	}

}
