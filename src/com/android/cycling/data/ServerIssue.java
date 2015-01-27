package com.android.cycling.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * web picture path stored in column urls
 * @author wsl
 *
 */
public class ServerIssue extends BmobObject{
	
	private static final long serialVersionUID = 65535l;
	
	private ServerUser user;
	
	private String name;
	private String level;
	private String price;
	private String phone;
	private String description;
//	private long date;
	private int type;
	private ArrayList<String> urls;
	
	public ServerIssue() {
		super();
		urls = new ArrayList<String>();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
//	public long getDate() {
//		return date;
//	}
//	public void setDate(long date) {
//		this.date = date;
//	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public ServerUser getUser() {
		return user;
	}
	public void setUser(ServerUser user) {
		this.user = user;
	}
	
	public void addPicture(String url) {
//		int index = urls.indexOf(url);
//		if(index == -1) {
//			urls.add(url);
//		}
		urls.add(url);
	}
	
	public void addPictures(Collection<String> urls) {
		this.urls.addAll(urls);
	}
	
	public ArrayList<String> getPictures() {
		return urls;
	}
	
	public boolean hasPictures() {
		if(urls == null || urls.isEmpty()) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return super.toString() + "["
				+ "name: " + name + "\n"
				+ "level: " + level+ "\n"
				+ "price: " + price + "\n"
				+ "phone: " + phone + "\n"
				+ "description: " + description + "\n"
				+ "updateTime: " + getUpdatedAt() + "\n"
				+ "type: " + type  + "\n"
				+ "url: " + urls.toString() + "\n"
				+ "user: " + user + "\n"
				+ "]"
				;
	}
	
	
}
