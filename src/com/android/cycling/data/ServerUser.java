package com.android.cycling.data;

import cn.bmob.v3.BmobUser;

public class ServerUser extends BmobUser{

	private static final long serialVersionUID = 1L;
	
	//sex
	private boolean isMale;
	//path of head picture
	private String avatar;
	private String age;
	private String signature;
	private String location;
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
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	@Override
	public String toString() {
		return super.toString() + "["
				+ "id: " + getObjectId() 
				+ "username：" + getUsername()
				+ "email: " + getEmail()
				+ "isMale: " + isMale
				+ "avatar: " + avatar
				+ "age: " + age
				+ "]";
	}
	
}