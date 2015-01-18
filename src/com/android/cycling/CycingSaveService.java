package com.android.cycling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import com.android.cycling.data.ServerIssue;
import com.android.cycling.data.ServerUser;
import com.android.cycling.provider.CyclingConstants;
import com.android.cycling.provider.CyclingConstants.Issue;
import com.android.cycling.provider.CyclingConstants.Photo;
import com.android.cycling.provider.CyclingConstants.User;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

public class CycingSaveService extends IntentService{
	
	private static final String TAG = CycingSaveService.class.getSimpleName();
	
	private static final String ACTION_SYNC_ISSUE = "syncIssue";//from bmob server
	
	private static final String ACTION_SAVE_ISSUE = "saveIssue";
	private static final String EXTRA_ISSUE_NAME = "issueName";
	private static final String EXTRA_ISSUE_LEVEL = "issueLevel";
	private static final String EXTRA_ISSUE_PRICE = "issuePrice";
	private static final String EXTRA_ISSUE_PHONE = "issuePhone";
	private static final String EXTRA_ISSUE_PHOTO = "issuePhoto";
	private static final String EXTRA_ISSUE_TYPE = "issueType";
	private static final String EXTRA_ISSUE_DESCRIPTION = "issueDescription";
	private static final String EXTRA_ISSUE_DATE = "date";
	private static final String EXTRA_ISSUE_SERVER_ID = "issueServerId";

	public CycingSaveService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final String action = intent.getAction();
		if(action.equals(ACTION_SAVE_ISSUE)) {
			saveIssue(intent);
		} else if(action.equals(ACTION_SYNC_ISSUE)) {
			syncIssue(intent);
		}
	}
	
	public static Intent createSyncIssueIntent(Context context) {
		Intent i = new Intent(context, CycingSaveService.class);
		i.setAction(ACTION_SYNC_ISSUE);
		
		return i;
	}
	
	private void syncIssue(Intent intent) {
		BmobQuery<ServerIssue> query = new BmobQuery<ServerIssue>();
		query.include("user");
		query.findObjects(this, new FindListener<ServerIssue>() {
			
			@Override
			public void onSuccess(List<ServerIssue> arg0) {
				logW("query success---result: " + arg0);
				syncContinue(arg0);
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				logW("query failed error: " + arg1);
			}
		});
	}
	
	/**
	 * First reconstruct issue object
	 * then delete locale data,last insert server data
	 * @param objects
	 */
	private void syncContinue(List<ServerIssue> objects) {
		if(objects == null || objects.isEmpty()) {
			return;
		}
		
		getContentResolver().delete(CyclingConstants.Issue.CONTENT_URI, null, null);
		insertServerIssueToDb(objects);
	}
	
	private void insertServerIssueToDb(List<ServerIssue> objects) {
		final ContentResolver resolver = getContentResolver();
		HashMap<String, ServerUser> userMap = new HashMap<String, ServerUser>();
		for(ServerIssue issue : objects){
			String name = issue.getName();
			String level = issue.getLevel();
			String price = issue.getPrice();
			String phone = issue.getPhone();
			int type = issue.getType();
			String description = issue.getDescription();
			String serverId = issue.getObjectId();
			long date = issue.getDate();
			
			ServerUser user = issue.getUser();
			String userId = user.getObjectId();
			if(!userMap.containsKey(userId)) {
				ContentValues values = new ContentValues();
				values.put(User.AVATAR, user.getAvatar());
				values.put(User.USERNAME, user.getUsername());
				values.put(User.EMAIL, user.getEmail());
				values.put(User.SERVER_ID, user.getObjectId());
//				if (!TextUtils.isEmpty(avatar)) {
//					values.put(User.AVATAR, avatar);
//				}
				resolver.insert(User.CONTENT_URI, values);
			}
			
			if(issue.hasPictures()) {
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
						.withValue(Issue.SERVER_ID, serverId)
						.withValue(Issue.USER_ID, userId)
						.build();
				operations.add(op);
				for(String pic : issue.getPictures()) {
					op = ContentProviderOperation.newInsert(Photo.CONTENT_URI)
							.withValueBackReference(Photo.ISSUE_ID, 0)
							.withValue(Photo.URI, pic)
							.build();
					operations.add(op);
				}
				try {
					ContentProviderResult[] results = resolver
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
				values.put(Issue.SERVER_ID, serverId);
				values.put(Issue.USER_ID, userId);
				resolver.insert(Issue.CONTENT_URI, values);
			}
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
		String serverId = intent.getStringExtra(EXTRA_ISSUE_SERVER_ID);//server issue id
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
					.withValue(Issue.SERVER_ID, serverId)
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
			values.put(Issue.SERVER_ID, serverId);
			getContentResolver().insert(Issue.CONTENT_URI, values);
		}
	}
	
	public static Intent createSaveIssueIntent(Context context, String name, 
			String level, String price, String description, long date, String phone, 
			int type, String serverId, String[] pictures) {
		Intent i = new Intent(context, CycingSaveService.class);
		i.setAction(ACTION_SAVE_ISSUE);
		i.putExtra(EXTRA_ISSUE_NAME, name);
		i.putExtra(EXTRA_ISSUE_LEVEL, level);
		i.putExtra(EXTRA_ISSUE_PRICE, price);
		i.putExtra(EXTRA_ISSUE_PHONE, phone);
		i.putExtra(EXTRA_ISSUE_TYPE, type);
		i.putExtra(EXTRA_ISSUE_DESCRIPTION, description);
		i.putExtra(EXTRA_ISSUE_DATE, date);
		i.putExtra(EXTRA_ISSUE_SERVER_ID, serverId);
		if(pictures != null && pictures.length >= 0) {
			i.putExtra(EXTRA_ISSUE_PHOTO, pictures);
		}
		return i;
	}
	
	private static final boolean DEBUG = true;
	private static void logW(String msg) {
		if(DEBUG) {
		android.util.Log.d(TAG, msg);
		}
	}

}
