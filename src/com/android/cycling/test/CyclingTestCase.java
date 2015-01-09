package com.android.cycling.test;

import cn.bmob.v3.listener.SaveListener;

import com.android.cycling.data.CyclingUser;

import android.test.AndroidTestCase;
import android.util.Log;

public class CyclingTestCase extends AndroidTestCase{
	
	private static final String TAG = CyclingTestCase.class.getSimpleName();
	
	public void testRegiste() {
		String email = "12345678@qq.com";
		String password = "111111";
		boolean isMale = false;
		CyclingUser user = new CyclingUser();
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
		CyclingUser user = new CyclingUser();
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
	
//	public void testEmailLogin() {
//		String email = "12345678@qq.com";
//		String password = "111111";
//		CyclingUser user = new CyclingUser();
//		user.setEmail(email);
//		user.setPassword(password);
//		user.login(getContext(), new SaveListener() {
//
//			@Override
//			public void onFailure(int arg0, String arg1) {
//				logW("email login success");
//			}
//
//			@Override
//			public void onSuccess() {
//				logW("email login failed");
//			}
//			
//		});
//	}
	
	private static void logW(String msg) {
		Log.d(TAG, msg);
	}

}
