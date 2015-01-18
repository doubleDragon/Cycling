package com.android.cycling.secondhand;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.cycling.R;
import com.android.cycling.secondhand.IssueListLoader.IssueResult;
import com.android.cycling.util.DateUtils;
import com.android.cycling.widget.SimpleGridView;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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
	
	public IssueListAdapter(Context context) {
		super(context, R.layout.issue_list_item);
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		
		mDescriptionPrefix = mContext.getString(R.string.description);
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
	
	/**
     * Cache of the children views of a contact detail entry represented by a
     */
    public static class IssueListItemViewCache {
    	
    	private final TextView issueName;
    	private final TextView level;
    	private final TextView price;
    	private final TextView description;
    	private final TextView date;
    	private final TextView type;
    	private final SimpleGridView photos;


        public IssueListItemViewCache(View view) {
        	issueName = (TextView) view.findViewById(R.id.issue_name);
        	level = (TextView) view.findViewById(R.id.level);
        	price = (TextView) view.findViewById(R.id.price);
        	description = (TextView) view.findViewById(R.id.description);
        	date = (TextView) view.findViewById(R.id.date);
        	type = (TextView) view.findViewById(R.id.type);
        	
        	photos = (SimpleGridView) view.findViewById(R.id.photos);
        }

    }

}
