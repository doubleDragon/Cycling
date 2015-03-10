package com.android.cycling;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.android.cycling.data.ServerIssue;
import com.android.cycling.data.ServerUser;
import com.android.cycling.interactions.Event;
import com.android.cycling.provider.CyclingConstants;
import com.android.cycling.provider.CyclingConstants.Issue;
import com.android.cycling.provider.CyclingConstants.Photo;
import com.android.cycling.provider.CyclingConstants.User;

import de.greenrobot.event.EventBus;

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

	public CycingSaveService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final String action = intent.getAction();
		if(action.equals(ACTION_SYNC_ISSUE)) {
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
		query.order("-updatedAt");
		query.include("user");
		query.findObjects(this, new FindListener<ServerIssue>() {
			
			@Override
			public void onSuccess(List<ServerIssue> arg0) {
				logW("query success---result: " + arg0);
				eventToIssueListFragment();
				syncContinue(arg0);
				
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				logW("query failed error: " + arg1);
				eventToIssueListFragment();
			}
		});
	}
	
	private void eventToIssueListFragment() {
		EventBus.getDefault().post(new Event.PullListViewEvent());
	}
	
	/**
	 * First reconstruct issue object
	 * then delete locale data,last insert server data
	 * @param objects
	 */
	private void syncContinue(List<ServerIssue> objects) {
		getContentResolver().delete(CyclingConstants.Issue.CONTENT_URI, null, null);
		
		if(objects == null || objects.isEmpty()) {
			return;
		}
		saveIssue(objects);
	}
	
	private long convertServerUpdateTime(String time) {
		long currentTime = System.currentTimeMillis();
		if(TextUtils.isEmpty(time)) {
			return currentTime;
		}
		
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = f.parse(time);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return currentTime;
		}
	}
	
	private void saveIssue(List<ServerIssue> objects) {
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
			long date = convertServerUpdateTime(issue.getUpdatedAt());
			Log.e("tag", "server date:" + date);
			
			ServerUser user = issue.getUser();
			String userId = user.getObjectId();
			if(!userMap.containsKey(userId)) {
				ContentValues values = new ContentValues();
				values.put(User._ID, user.getObjectId());
				values.put(User.AVATAR, user.getAvatar());
				values.put(User.USERNAME, user.getUsername());
				values.put(User.EMAIL, user.getEmail());
				resolver.insert(User.CONTENT_URI, values);
			}
			
			if(issue.hasPictures()) {
				ArrayList<ContentProviderOperation> operations = new  ArrayList<ContentProviderOperation>();
				ContentProviderOperation op;
				op = ContentProviderOperation.newInsert(Issue.CONTENT_URI)
						.withValue(Issue._ID, serverId)
						.withValue(Issue.NAME, name)
						.withValue(Issue.LEVEL, level)
						.withValue(Issue.PRICE, price)
						.withValue(Issue.PHONE, phone)
						.withValue(Issue.TYPE, type)
						.withValue(Issue.DESCRIPTION, description)
						.withValue(Issue.DATE, date)
						.withValue(Issue.USER_ID, userId)
						.build();
				operations.add(op);
				for(String pic : issue.getPictures()) {
					op = ContentProviderOperation.newInsert(Photo.CONTENT_URI)
							.withValue(Photo.ISSUE_ID, issue.getObjectId())
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
				values.put(Issue._ID, serverId);
				values.put(Issue.NAME, name);
				values.put(Issue.LEVEL, level);
				values.put(Issue.PRICE, price);
				values.put(Issue.PHONE, phone);
				values.put(Issue.TYPE, type);
				values.put(Issue.DESCRIPTION, description);
				values.put(Issue.DATE, date);
				values.put(Issue.USER_ID, userId);
				resolver.insert(Issue.CONTENT_URI, values);
			}
		}
	}
	
	private static final boolean DEBUG = true;
	private static void logW(String msg) {
		if(DEBUG) {
		android.util.Log.d(TAG, msg);
		}
	}

}
