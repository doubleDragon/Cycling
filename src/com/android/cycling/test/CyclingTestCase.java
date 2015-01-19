package com.android.cycling.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.android.cycling.data.ServerIssue;
import com.android.cycling.data.ServerUser;

import android.test.AndroidTestCase;
import android.util.Log;

public class CyclingTestCase extends AndroidTestCase{
	
	private static final String TAG = CyclingTestCase.class.getSimpleName();
	
	public void testRegiste() {
		String email = "12345678@qq.com";
		String password = "111111";
		boolean isMale = false;
		ServerUser user = new ServerUser();
		user.setUsername(email);
		user.setPassword(password);
		user.setEmail(email);
		user.setEmailVerified(true);
		user.setMale(isMale);
		user.setAvatar("");
		user.signUp(getContext(), new SaveListener() {
			
			@Override
			public void onSuccess() {
				logW("registe success");
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				logW("registe failed");
			}
		});
	}
	
	public void testUserNameLogin() {
		String username = "12345678@qq.com";
		String password = "111111";
		ServerUser user = new ServerUser();
		user.setUsername(username);
		user.setPassword(password);
		user.login(getContext(), new SaveListener() {

			@Override
			public void onFailure(int arg0, String arg1) {
				logW("username login success");
			}

			@Override
			public void onSuccess() {
				logW("username login failed");
			}
			
		});
	}
	
	public void testEmailLogin() {
		String email = "12345678@qq.com";
		String password = "111111";
		ServerUser user = new ServerUser();
		user.setEmail(email);
		user.setPassword(password);
		user.login(getContext(), new SaveListener() {

			@Override
			public void onFailure(int arg0, String arg1) {
				logW("email login success");
			}

			@Override
			public void onSuccess() {
				logW("email login failed");
			}
			
		});
	}
	
	public void testUploadOneFile() {
		File file = new File("/sdcard/ic_error.png");
		final BmobFile bmobFile = new BmobFile(file);
		bmobFile.uploadblock(getContext(), new UploadFileListener() {

			@Override
			public void onSuccess() {
				Log.i(TAG, "单个文件上传成功--url: " + bmobFile.getFileUrl(getContext()));
			}

			@Override
			public void onProgress(Integer arg0) {
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				Log.i(TAG, "单个文件上传失败");
			}

		});
	}
	
	public void testSaveIssueToServer() {
		
		List<String> urls = new ArrayList<String>();
		urls.add("test1");
		urls.add("test2");

		final ServerIssue issue = new ServerIssue();
		issue.setName("bmc");
		issue.setLevel("8新");
		issue.setPrice("16800");
		issue.setDescription("随便看看把");
		issue.setPhone("18600000000");
		issue.setType(0);
		issue.addPictures(urls);

		issue.save(getContext(), new SaveListener() {

			@Override
			public void onFailure(int arg0, String arg1) {
				logW("save issue failed");
			}

			@Override
			public void onSuccess() {
				logW("save issue success---updateTime: " + issue.getUpdatedAt());
				
			}

		});
	}
	
//	public void testSaveIssueAndPictureToServer() {
//		ServerIssue issue = new ServerIssue();
//		issue.setName("bmc");
//		issue.setLevel("8新");
//		issue.setPrice("16800");
//		issue.setDescription("随便看看把");
//		issue.setPhone("18600000000");
//		issue.setType(0);
//
//		issue.save(getContext(), new SaveListener() {
//
//			@Override
//			public void onFailure(int arg0, String arg1) {
//				logW("save issue failed");
//			}
//
//			@Override
//			public void onSuccess() {
//				logW("save issue success");
//			}
//
//		});
//	}
	
	private static void logW(String msg) {
		Log.d(TAG, msg);
	}

}
