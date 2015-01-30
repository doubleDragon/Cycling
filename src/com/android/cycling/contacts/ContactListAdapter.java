package com.android.cycling.contacts;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.cycling.R;
import com.android.cycling.data.ServerUser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ContactListAdapter extends ArrayAdapter<ServerUser> implements SectionIndexer{
	
	private final LayoutInflater mLayoutInflater;
	
	private DisplayImageOptions options;
	
	private List<ServerUser> mData;

	public ContactListAdapter(Context context) {
		super(context, R.layout.contact_list_item);
		
		mLayoutInflater = LayoutInflater.from(context);
		
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}
	
	public void setData(List<ServerUser> items) {
		clear();
		if(mData != null) {
			mData.clear();
			mData = null;
		}

        if (items == null) return;
        addAll(items);
        mData = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ServerUser user = getItem(position);
        View result;
        ContactListItemViewCache viewCache;
        if (convertView != null) {
            result = convertView;
            viewCache = (ContactListItemViewCache) result.getTag();
        } else {
            result = mLayoutInflater.inflate(R.layout.contact_list_item, parent, false);
            viewCache = new ContactListItemViewCache(result);
            result.setTag(viewCache);
        }
        ImageLoader.getInstance().displayImage(user.getAvatar(), viewCache.avatar, options);
        viewCache.name.setText(user.getUsername());
        
        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
        	viewCache.alpha.setVisibility(View.VISIBLE);
        	viewCache.alpha.setText(user.getSortLetters());
		} else {
			viewCache.alpha.setVisibility(View.GONE);
		}
        
		return result;
	}
	
	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mData.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == sectionIndex){
				return i;
			}
		}

		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return mData.get(position).getSortLetters().charAt(0);
	}
	
	
	private static class ContactListItemViewCache {
		public final TextView alpha;
		public final ImageView avatar;
		public final TextView name;
		public ContactListItemViewCache(View view) {
			alpha = (TextView) view.findViewById(R.id.alpha);
			avatar = (ImageView) view.findViewById(R.id.img_friend_avatar);
			name = (TextView) view.findViewById(R.id.tv_friend_name);
		}
	}

}
