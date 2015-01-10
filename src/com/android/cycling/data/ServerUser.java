package com.android.cycling.data;

import cn.bmob.v3.BmobUser;

public class ServerUser extends BmobUser{

	//sex
	private boolean isMale;
	//path of head picture
	private String avatar;
	public boolean isMale() {
		return isMale;
	}
	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	
}
