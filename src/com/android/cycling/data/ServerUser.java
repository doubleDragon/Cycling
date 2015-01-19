package com.android.cycling.data;

import cn.bmob.v3.BmobUser;

public class ServerUser extends BmobUser{

	private static final long serialVersionUID = 1L;
	
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
	@Override
	public String toString() {
		return super.toString() + "["
				+ "id: " + getObjectId() 
				+ "username：" + getUsername()
				+ "email: " + getEmail()
				+ "isMale: " + isMale
				+ "avatar: " + avatar
				+ "]";
	}
	
}
