package com.android.cycling.secondhand;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.Log;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.android.cycling.data.ServerIssue;
import com.android.cycling.util.UserUtils;

public class IssueManager {
	
	public static interface Listener {
		void onComplete(boolean success);
	}
	
	private static final String TAG = IssueManager.class.getSimpleName();
	private static final boolean DEBUG = true;
	
	private Context mContext;
	private Listener mListener;
	private boolean mResult;
	
	
	public void setListener(Listener listener) {
		mListener = listener;
	}
	
	public IssueManager(Context context) {
		this.mContext = context;
	}
	
	private boolean isNeedToUploadPhoto(String[] pictures) {
		if(pictures != null && pictures.length > 0) {
			return true;
		}
		return false;
	}
	
	public void saveIssueToServer(String name, String level, 
			String price, String description, String phone, 
			int type, final String[] pictures) {
		ServerIssue issue = new ServerIssue();
		issue.setName(name);
		issue.setLevel(level);
		issue.setPrice(price);
		issue.setDescription(description);
		issue.setPhone(phone);
		issue.setType(type);
		issue.setUser(UserUtils.getCurrentUser(mContext));
		
		final boolean needToUploadPhoto = isNeedToUploadPhoto(pictures);
		if(needToUploadPhoto) {
			uploadFiles(pictures, issue);
		} else {
			saveServerIssue(issue);
		}
	}
	
	private void saveServerIssue(ServerIssue issue) {
		issue.save(mContext, new IssueSaveListener(issue));
	}
	
	private void uploadFiles(final String[] pictStrings,  ServerIssue issue) {
		if(pictStrings.length == 1) {
			uploadOneFile(pictStrings[0], issue);
		} else {
			uploadFileInBatch(pictStrings, issue);
		}
	}
	
	/**
	 * Upload multipart files
	 * 
	 */
	private void uploadFileInBatch(String[] picStrings,final ServerIssue issue) {
		Bmob.uploadBatch(mContext, picStrings, new PictureUploadBatchListener(issue, picStrings.length));
	}
	
	/**
	 * Upload single file,and max size 200M
	 * @param picPath
	 */
	private void uploadOneFile(String picPath,ServerIssue issue) {
		File file = new File(picPath);
		if(!file.exists()) {
			//文件不存在
			mResult = false;
			callbackIssueResult();
		}
		BmobFile bmobFile = new BmobFile(file);
		bmobFile.uploadblock(mContext, new PictureUploadFileListener(issue, bmobFile));
	}
	
	private void callbackIssueResult() {
		if(mListener != null) {
			mListener.onComplete(mResult);
		}
	}
	
	private class PictureUploadBatchListener implements UploadBatchListener {
		
		private final ServerIssue serverIssue;
		private final int count;

		public PictureUploadBatchListener(ServerIssue serverIssue, int count) {
			super();
			this.serverIssue = serverIssue;
			this.count = count;
		}

		@Override
		public void onError(int arg0, String arg1) {
			logW("PictureUploadBatchListener---failed arg1:" + arg1);
			mResult = false;
			callbackIssueResult();
		}

		@Override
		public void onProgress(int arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			// 1、curIndex--表示当前第几个文件正在上传
			// 2、curPercent--表示当前上传文件的进度值（百分比）
			// 3、total--表示总的上传文件数
			// 4、totalPercent--表示总的上传进度（百分比）
		}

		@Override
		public void onSuccess(List<BmobFile> arg0, List<String> arg1) {
			logW("PictureUploadBatchListener---onSuccess arg1:" + arg1);
			int size = arg0.size();
			if(size != count) {
				//Is't not last uploaded file,just return
				return;
			}
			
			serverIssue.addPictures(new ArrayList<String>(arg1));
			logW("PictureUploadBatchListener---onSuccess hasPictures:" + serverIssue.hasPictures());
			saveServerIssue(serverIssue);
		}
		
	}
	
	private class PictureUploadFileListener extends UploadFileListener {
		
		private final ServerIssue serverIssue;
		private final BmobFile bmobFile;
		
		public PictureUploadFileListener(ServerIssue serverIssue, BmobFile bmobFile) {
			super();
			this.serverIssue = serverIssue;
			this.bmobFile = bmobFile;
		}

		@Override
		public void onFailure(int arg0, String arg1) {
			logW("PictureUploadFileListener---failed arg1:" + arg1);
			mResult = false;
			callbackIssueResult();
		}

		@Override
		public void onSuccess() {
			String url = bmobFile.getFileUrl(mContext);
			logW("PictureUploadFileListener---success---url: " + url);
			serverIssue.addPicture(url);
			saveServerIssue(serverIssue);
		}

		@Override
		public void onProgress(Integer arg0) {
			// 返回的上传进度（百分比）
		}
	}
	
	private class IssueSaveListener extends SaveListener {
		
		public final ServerIssue serverIssue;

		public IssueSaveListener(ServerIssue serverIssue) {
			super();
			this.serverIssue = serverIssue;
		}

		@Override
		public void onFailure(int arg0, String arg1) {
			logW("IssueSaveListener---save issue" + serverIssue + " failed---error:" + arg1);
			mResult = false;
			callbackIssueResult();
		}

		@Override
		public void onSuccess() {
			logW("IssueSaveListener---save issue" + serverIssue + " success");
			mResult = true;
			callbackIssueResult();
		}
	}
	
	private static void logW(String msg) {
		if(DEBUG) {
			Log.d(TAG, msg);
		}
	}
	
	
	
}
