package com.android.cycling.secondhand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.cycling.provider.CyclingConstants.Issue;
import com.android.cycling.provider.CyclingConstants.Photo;

import android.os.Handler;
import android.text.TextUtils;
import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;

public class IssueListLoader extends AsyncTaskLoader<ArrayList<IssueListLoader.Result>>{
	
	private static final int ISSUE_ID = 0;
	private static final int ISSUE_NAME = 1;
	private static final int ISSUE_LEVEL = 2;
	private static final int ISSUE_PRICE = 3;
	private static final int ISSUE_PHONE = 4;
	private static final int ISSUE_TYPE = 5;
	private static final int ISSUE_DESCRIPTION = 6;
	private static final int ISSUE_DATE = 7;
	private static final int ISSUE_SERVER_ID = 8;
	private static final int ISSUE_PHOTO = 9;
	
	private ArrayList<Result> mResults;
	
	public static class Result {
		public long _id;
		public String name;
		public String level;
		public String price;
		public String phone;
		public int type;
		public String description;
		public long date;
		public String serverId;
		public ArrayList<String> photoList;
		
	}
	
	public static final String[] PROJECTION = new String[] {
		Issue._ID,
		Issue.NAME,
		Issue.LEVEL,
		Issue.PRICE,
		Issue.PHONE,
		Issue.TYPE,
		Issue.DESCRIPTION,
		Issue.DATE,
		Issue.SERVER_ID,
		Photo.URI
	};
	
	private DataObserver mObserver;

	public IssueListLoader(Context context) {
		super(context);
	}
	
	@Override
    public void deliverResult(ArrayList<Result> results) {
        mResults = results;
        if (isStarted()) {
            // If the Loader is started, immediately deliver its results.
            super.deliverResult(results);
        }
    }

	@Override
    protected void onStartLoading() {
        if (mResults != null) {
            // If we currently have a result available, deliver it immediately.
            deliverResult(mResults);
        }
        
        if(mObserver == null) {
        	mObserver = new DataObserver(new Handler(), this);
        }

        if (takeContentChanged() || mResults == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }
	
	@Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }
	
	@Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated if needed.
        mResults = null;
        
        if(mObserver != null) {
        	getContext().getContentResolver().unregisterContentObserver(mObserver);
        }
    }

	@Override
	public ArrayList<Result> loadInBackground() {
		ArrayList<Result> issues = getIssues();
		return issues;
	}
	
	public ArrayList<Result> getIssues() {
		ContentResolver resolver = getContext().getContentResolver();
		Cursor c = resolver.query(Issue.CONTENT_URI, PROJECTION, null, null, Issue._ID + " ASC");
		ArrayList<Result> results = new ArrayList<Result>();
		String photo;
		long id;
		Result result;
		HashMap<Long, IssueListLoader.Result> issuesMap = new HashMap<Long, IssueListLoader.Result>();
		try {
			c.moveToPosition(-1);
			while(c.moveToNext()) {
				id = c.getInt(ISSUE_ID);
				photo = c.getString(ISSUE_PHOTO);
				if(issuesMap.containsKey(id)){
					if (TextUtils.isEmpty(photo)) {
						continue;
					}
					
					result = issuesMap.get(id);
					if(result.photoList == null) {
						result.photoList = new ArrayList<String>();
					}
					result.photoList.add(photo);
				} else {
					result = new Result();
					result._id = id;
					result.name = c.getString(ISSUE_NAME);
					result.level = c.getString(ISSUE_LEVEL);
					result.price = c.getString(ISSUE_PRICE);
					result.phone = c.getString(ISSUE_PHONE);
					result.type = c.getInt(ISSUE_TYPE);
					result.description = c.getString(ISSUE_DESCRIPTION);
					result.date = c.getLong(ISSUE_DATE);
					result.serverId = c.getString(ISSUE_SERVER_ID);
					
					if (!TextUtils.isEmpty(photo)) {
						result.photoList = new ArrayList<String>();
						result.photoList.add(photo);
					}
					
					results.add(result);
					issuesMap.put(id, result);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(c != null) {
				c.close();
			}
			issuesMap.clear();
		}
		return results;
	}
	
	public static boolean ContainChinese(CharSequence str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
	
	private static class DataObserver extends ContentObserver {
		
		private IssueListLoader mLoader;

		public DataObserver(Handler handler, IssueListLoader loader) {
			super(handler);
			mLoader = loader;
			
			mLoader.getContext().getContentResolver().registerContentObserver(Issue.CONTENT_URI, 
					true, this);
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			mLoader.onContentChanged();
		}

	}

}
