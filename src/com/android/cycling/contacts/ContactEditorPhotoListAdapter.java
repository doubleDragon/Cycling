package com.android.cycling.contacts;

import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.android.cycling.R;
import com.android.cycling.pictures.SimpleGridViewListAdapter;

public class ContactEditorPhotoListAdapter extends SimpleGridViewListAdapter{

	public ContactEditorPhotoListAdapter(Context context) {
		super(context, R.layout.contact_editor_photo_list_item);
	}
	
	/**
     *  Max count is 6
     */
    @Override
    protected void ensureDataLenght(List<String> data) {
        Iterator<String> it = data.iterator();
        int i = 0;
        while(it.hasNext()) {
            it.next();
            if(i > 5) {
                it.remove();
            }
            i++;
        }
    }

}
