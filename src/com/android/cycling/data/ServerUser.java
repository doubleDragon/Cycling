package com.android.cycling.data;

import java.util.ArrayList;
import java.util.Collection;

import cn.bmob.im.bean.BmobChatUser;

public class ServerUser extends BmobChatUser{

	private static final long serialVersionUID = 65536l;
	
	//sex
	private boolean isMale;
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
				+ "id: " + getObjectId() + "\n"
				+ "username：" + getUsername() + "\n"
				+ "email: " + getEmail() + "\n"
				+ "isMale: " + isMale + "\n"
				+ "avatar: " + getAvatar() + "\n"
				+ "age: " + age + "\n"
				+ "gallery: " + gallery + "\n"
				+ "]";
	}
	
}