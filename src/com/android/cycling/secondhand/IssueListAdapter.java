package com.android.cycling.secondhand;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.cycling.R;
import com.android.cycling.activities.ContactEditorActivity;
import com.android.cycling.secondhand.IssueListLoader.IssueResult;
import com.android.cycling.secondhand.IssueListLoader.UserResult;
import com.android.cycling.util.DateUtils;
import com.android.cycling.widget.RoundedCornerImageView;
import com.android.cycling.widget.RoundedImageView;
import com.android.cycling.widget.SimpleGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IssueListAdapter extends ArrayAdapter<IssueResult>{
	
	private static final String TAG = IssueListAdapter.class.getSimpleName();
	
	private final Context mContext;
	private final LayoutInflater mLayoutInflater;
	
	private final String mDescriptionPrefix;
	
	private HashMap<Integer, IssueListPhotoAdapter> mAdapterMap = 
			new HashMap<Integer, IssueListPhotoAdapter>();
	
	private DisplayImageOptions options;
	
	public IssueListAdapter(Context context) {
		super(context, R.layout.issue_list_item);
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		
		mDescriptionPrefix = mContext.getString(R.string.description);
		
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}
	
	public void setData(ArrayList<IssueResult> items) {
		clear();

        if (items == null) return;
        addAll(items);
	}
	
	@Override
    public IssueResult getItem(int position) {
        return super.getItem(position);
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		IssueResult item = getItem(position);
        View result;
        IssueListItemViewCache viewCache;
        if (convertView != null) {
            result = convertView;
            viewCache = (IssueListItemViewCache) result.getTag();
        } else {
            result = mLayoutInflater.inflate(R.layout.issue_list_item, parent, false);
            viewCache = new IssueListItemViewCache(result);
            result.setTag(viewCache);
        }
        UserResult user = item.user;
        if(user != null) {
        	viewCache.username.setText(user.username);
        	viewCache.avatar.setOnClickListener(new AvatarClickListener(user._id));
        	ImageLoader.getInstance().displayImage(user.avatar, viewCache.avatar, options);
        }
        viewCache.issueName.setText(item.name);
        viewCache.level.setText(item.level);
        viewCache.price.setText(item.price);
        final String checkedDescription = checkDescription(item.description);
        if(TextUtils.isEmpty(checkedDescription)) {
        	viewCache.description.setVisibility(View.GONE);
        } else {
        	viewCache.description.setVisibility(View.VISIBLE);
        	viewCache.description.setText(checkedDescription);
        }
        viewCache.type.setText(convertTypeToRessource(item.type));
        final String dateToDisplay = DateUtils.getDisplayTime(item.date);
        if(!TextUtils.isEmpty(dateToDisplay)) {
        	viewCache.date.setVisibility(View.VISIBLE);
        	viewCache.date.setText(DateUtils.getDisplayTime(item.date));
        } else {
        	viewCache.date.setVisibility(View.GONE);
        }
        
        if(item.photoList == null || item.photoList.size() < 1) {
        	viewCache.photos.setVisibility(View.GONE);
        } else {
        	viewCache.photos.setVisibility(View.VISIBLE);
        	IssueListPhotoAdapter photoAdapter;
        	if(mAdapterMap.containsKey(position)) {
        		photoAdapter = mAdapterMap.get(position);
        		if(photoAdapter == null) {
        			photoAdapter = new IssueListPhotoAdapter(mContext);
        		}
        	} else {
        		photoAdapter = new IssueListPhotoAdapter(mContext);
        		mAdapterMap.put(position, photoAdapter);
        	}
        	photoAdapter.setData(new ArrayList<String>(item.photoList));
	        viewCache.photos.setAdapter(photoAdapter);
        }
        
		return result;
	}
	
	/**
	 * @param type
	 * @return resource id
	 */
	private int convertTypeToRessource(int type) {
		switch(type) {
		case 0:
			return R.string.type_all;
		case 1:
			return R.string.type_parts;
		case 2:
			return R.string.type_buy;
		default:
			throw new IllegalArgumentException("wrong issue type");
		}
	}
	
	/**
	 * Appedn prefix
	 * @return description
	 */
	private String checkDescription(String description) {
		if(TextUtils.isEmpty(description)) return null;
		
		StringBuilder sb = new StringBuilder();
		sb.append(mContext.getString(R.string.description));
		sb.append(" ");
		sb.append(description);
		return sb.toString();
	}
	
	private class AvatarClickListener implements OnClickListener {
		
		private final String userServerId;
		
		public AvatarClickListener(String userServerId) {
			this.userServerId = userServerId;
		}

		@Override
		public void onClick(View v) {
			//intent to ContactEditorActivity
			Intent i = new Intent(mContext, ContactEditorActivity.class);
			i.putExtra(ContactEditorActivity.EXTRA_SERVER_ID, userServerId);
			mContext.startActivity(i);
		}
		
	}
	
	/**
     * Cache of the children views of a contact detail entry represented by a
     */
    public static class IssueListItemViewCache {
    	
    	private final RoundedImageView avatar;
    	private final TextView username;
    	private final TextView issueName;
    	private final TextView level;
    	private final TextView price;
    	private final TextView description;
    	private final TextView date;
    	private final TextView type;
    	private final SimpleGridView photos;


        public IssueListItemViewCache(View view) {
        	avatar = (RoundedImageView) view.findViewById(R.id.avatar);
        	issueName = (TextView) view.findViewById(R.id.issue_name);
        	username = (TextView) view.findViewById(R.id.username);
        	level = (TextView) view.findViewById(R.id.level);
        	price = (TextView) view.findViewById(R.id.price);
        	description = (TextView) view.findViewById(R.id.description);
        	date = (TextView) view.findViewById(R.id.date);
        	type = (TextView) view.findViewById(R.id.type);
        	
        	photos = (SimpleGridView) view.findViewById(R.id.photos);
        }

    }

}
