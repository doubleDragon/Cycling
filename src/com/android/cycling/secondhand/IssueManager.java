package com.android.cycling.secondhand;

import android.content.Context;
import android.content.Intent;
import cn.bmob.v3.listener.SaveListener;

import com.android.cycling.CycingSaveService;
import com.android.cycling.data.ServerIssue;

public class IssueManager {
	
	private Context mContext;
	
	public IssueManager(Context context) {
		this.mContext = context;
	}
	
	public void saveIssueToServer(String name, String level, 
			String price, String description, long date, String phone, 
			int type, String[] pictures, SaveListener listener) {
		ServerIssue issue = new ServerIssue();
		issue.setName(name);
		issue.setLevel(level);
		issue.setPrice(price);
		issue.setDescription(description);
		issue.setPhone(phone);
		issue.setType(type);
		
		saveIssueToServer(issue, listener);
		//not process pictures
	}

	public void saveIssueToServer(ServerIssue issue, SaveListener listener) {
		if(issue == null) {
			return;
		}
		issue.save(mContext,listener);
	}
	
}
