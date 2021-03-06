package com.android.cycling.secondhand;

import java.util.Iterator;
import java.util.List;

import com.android.cycling.R;
import com.android.cycling.pictures.SimpleGridViewListAdapter;
import android.content.Context;

public class IssueListPhotoAdapter extends SimpleGridViewListAdapter{
	
	public IssueListPhotoAdapter(Context context) {
		super(context, R.layout.issue_list_photo_list_item);
	}
	
	public void setData(List<String> data) {
		clear();
		if(data == null) return;
		ensureDataLenght(data);
		addAll(data);
		
		notifyDataSetChanged();
	}
	
	/**
	 *  Max length is 3 in horizontal
	 */
	@Override
	protected void ensureDataLenght(List<String> data) {
		Iterator<String> it = data.iterator();
		int i = 0;
		while(it.hasNext()) {
			it.next();
			if(i > 2) {
				it.remove();
			}
			i++;
		}
	}
	
}
