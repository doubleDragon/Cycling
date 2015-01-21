package com.android.cycling.data;

import java.util.ArrayList;
import java.util.Collection;

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
	
	private ArrayList<String> gallery;
	
	public ServerUser() {
		super();
		gallery = new ArrayList<String>();
	}
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
	
	public void addGalleryPhoto(String url) {
		gallery.add(url);
	}
	
	public void addGalleryPhotos(Collection<String> urls) {
		this.gallery.addAll(urls);
	}
	
	public ArrayList<String> getGallery() {
		return gallery;
	}
	
	public boolean hasPhotoInGallery() {
		if(gallery == null || gallery.isEmpty()) {
			return false;
		}
		return true;
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