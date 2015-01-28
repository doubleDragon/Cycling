package com.android.cycling.contacts;

import android.content.Context;

import com.android.cycling.R;
import com.android.cycling.pictures.SimpleGridViewListAdapter;

public class ContactDetailPhotoListAdapter extends SimpleGridViewListAdapter{

	public ContactDetailPhotoListAdapter(Context context) {
		super(context, R.layout.contact_detail_photo_list_item);
	}

}
