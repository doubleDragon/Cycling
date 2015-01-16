package com.android.cycling.util;

import com.android.cycling.activities.LoginActivity;

import android.content.Context;
import android.content.Intent;
import cn.bmob.v3.BmobUser;

public final class UserUtils {
	
	/**
	 * Check the cache user reference
	 * @return
	 */
	public static boolean isNeedLogin(Context context) {
		BmobUser bmobUser = BmobUser.getCurrentUser(context);
		if(bmobUser == null) {
			return true;
		}
		return false;
	}
	
	public static void intentToLogin(Context context) {
		Intent i = new Intent(context, LoginActivity.class);
		context.startActivity(i);
	}

}
