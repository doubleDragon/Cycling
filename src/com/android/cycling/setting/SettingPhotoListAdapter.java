package com.android.cycling.setting;

import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.android.cycling.R;
import com.android.cycling.pictures.SimpleGridViewListAdapter;

public class SettingPhotoListAdapter extends SimpleGridViewListAdapter{

	public SettingPhotoListAdapter(Context context) {
		super(context, R.layout.setting_photo_list_item);
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
