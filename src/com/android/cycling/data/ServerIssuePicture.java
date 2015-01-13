package com.android.cycling.data;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class ServerIssuePicture extends BmobObject{
	
	private static final long serialVersionUID = 1L;

	private ServerIssue serverIssue;
	private BmobFile picture;
	public BmobFile getPicture() {
		return picture;
	}
	public void setPicture(BmobFile picture) {
		this.picture = picture;
	}
	public ServerIssue getServerIssue() {
		return serverIssue;
	}
	public void setServerIssue(ServerIssue serverIssue) {
		this.serverIssue = serverIssue;
	}
	
}
