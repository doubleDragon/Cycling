package com.android.cycling.secondhand;

import java.util.List;

import com.android.cycling.R;
import com.android.cycling.pictures.SimpleGridViewListAdapter;
import android.content.Context;

public class IssueEditorPhotoListAdapter extends SimpleGridViewListAdapter{
	
	private List<String> mData;

	public IssueEditorPhotoListAdapter(Context context) {
		super(context, R.layout.photo_list_item);
	}
	
	@Override
	public void setData(List<String> data) {
		super.setData(data);
		mData = data;
	}

	/**
	 * return all picture path,not uri path
	 */
	public String[] getAllData() {
		if(mData == null) {
			return null;
		}
		
		int size =mData.size();
		if(size == 1) {
			//last photo not save to db
			return null;
		} else if(size == 6) {
			int j = 0;
			String[] result = new String[size];
			for(String dataString : mData){
				result[j] = dataString.substring(7);
				j++;
			}
			return result;
		} else {
			String[] result = new String[size - 1];
			for (int i = 0; i < size - 1; i++) {
				result[i] = mData.get(i).substring(7);
			}
			return result;	
		}
	}
	
	public boolean hasPictures() {
		final String[] temp = getAllData();
		if(temp == null || temp.length == 0) {
			return false;
		}
		return true;
	}
}
