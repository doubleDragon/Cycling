package com.android.cycling.secondhand;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.android.cycling.CycingSaveService;
import com.android.cycling.data.ServerIssue;
import com.android.cycling.data.ServerIssuePicture;

public class IssueManager {
	
	public static interface Listener {
		void onComplete(boolean result);
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
			String price, String description, long date, String phone, 
			int type, final String[] pictures) {
		final ServerIssue issue = new ServerIssue();
		issue.setName(name);
		issue.setLevel(level);
		issue.setPrice(price);
		issue.setDescription(description);
		issue.setPhone(phone);
		issue.setType(type);
		
		issue.save(mContext, new SaveListener() {

			@Override
			public void onFailure(int arg0, String arg1) {
				logW("save issue failed");
			}

			@Override
			public void onSuccess() {
				final boolean needToUploadPhoto = isNeedToUploadPhoto(pictures);
				if(needToUploadPhoto) {
					uploadFiles(pictures, issue);
				}
			}
			
		});
		
		
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
		Bmob.uploadBatch(mContext, picStrings, new UploadBatchListener() {

			@Override
			public void onSuccess(List<BmobFile> files, List<String> urls) {
				// TODO Auto-generated method stub
				// 1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
				// 2、urls-上传文件的服务器地址
				saveServerPicture(files, issue);
				logW("批上传成功" + ",urls：" + urls);
				mResult = true;
				saveIssueResult();
			}

			@Override
			public void onError(int statuscode, String errormsg) {
				// TODO Auto-generated method stub
				logW("错误码" + statuscode + ",错误描述：" + errormsg);
				mResult = false;
				saveIssueResult();
			}

			@Override
			public void onProgress(int curIndex, int curPercent, int total,
					int totalPercent) {
				// TODO Auto-generated method stub
				// 1、curIndex--表示当前第几个文件正在上传
				// 2、curPercent--表示当前上传文件的进度值（百分比）
				// 3、total--表示总的上传文件数
				// 4、totalPercent--表示总的上传进度（百分比）
			}
		});
	}
	
	private void saveServerPicture(List<BmobFile> files, final ServerIssue issue) {
		int size = files.size();
		
		final ServerIssuePicture picture = new ServerIssuePicture();
		picture.setPicture(files.get(size - 1));
		picture.setServerIssue(issue);
		picture.save(mContext, new SaveListener() {

			@Override
			public void onFailure(int arg0, String arg1) {
				logW("save " + picture + " failed---errorcode:" + arg0 + "---"
						+ arg1);
			}

			@Override
			public void onSuccess() {
				logW("save " + picture + " success");
			}

		});
	}
	
	
	/**
	 * Upload single file,and max size 200M
	 * @param picPath
	 */
	private void uploadOneFile(String picPath,final ServerIssue issue) {
		File file = new File(picPath);
		final BmobFile bmobFile = new BmobFile(file);
		bmobFile.uploadblock(mContext, new UploadFileListener() {

		    @Override
		    public void onSuccess() {
		    	final String fileUrl = bmobFile.getFileUrl(mContext);
		    	logW("上传文件成功:" + fileUrl);
		    	
		    	ServerIssuePicture picture = new ServerIssuePicture();
		    	picture.setServerIssue(issue);
		    	picture.setPicture(bmobFile);
		    	saveBmobObject(picture);
		    }

		    @Override
		    public void onProgress(Integer value) {
		        // TODO Auto-generated method stub
		        // 返回的上传进度（百分比）
		    }

		    @Override
		    public void onFailure(int code, String msg) {
		    	logW("上传文件失败：" + msg);
		    	mResult = false;
				saveIssueResult();
		    }
		});
	}
	
	private void saveBmobObject(final BmobObject obj) {
		obj.save(mContext, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				logW("-->创建数据成功：" + obj.getObjectId());
				mResult = true;
				saveIssueResult();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				logW("-->创建数据失败：" + arg0 + ",msg = " + arg1);
				mResult = false;
				saveIssueResult();
			}
		});
	}
	
	private void saveIssueResult() {
		if(mListener != null) {
			mListener.onComplete(mResult);
		}
	}
	
	private static void logW(String msg) {
		if(DEBUG) {
			Log.d(TAG, msg);
		}
	}
	
	
	
}
