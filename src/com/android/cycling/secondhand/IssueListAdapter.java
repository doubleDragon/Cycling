package com.android.cycling.secondhand;

import java.util.ArrayList;

import com.android.cycling.R;
import com.android.cycling.secondhand.IssueListLoader.Result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IssueListAdapter extends ArrayAdapter<Result>{
	
	private static final String TAG = IssueListAdapter.class.getSimpleName();
	
	private final Context mContext;
	private final LayoutInflater mLayoutInflater;
	
	public IssueListAdapter(Context context) {
		super(context, R.layout.issue_list_item);
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	public void setData(ArrayList<Result> items) {
		clear();

        if (items == null) return;
        addAll(items);
	}
	
	@Override
    public Result getItem(int position) {
        return super.getItem(position);
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Result item = getItem(position);
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
        
        viewCache.name.setText(item.name);
        viewCache.level.setText(item.level);
        viewCache.price.setText(item.price);
        viewCache.description.setText(item.description);
        
		return result;
	}
	
	/**
     * Cache of the children views of a contact detail entry represented by a
     */
    public static class IssueListItemViewCache {
    	
    	private final TextView name;
    	private final TextView level;
    	private final TextView price;
    	private final TextView description;


        public IssueListItemViewCache(View view) {
        	name = (TextView) view.findViewById(R.id.name);
        	level = (TextView) view.findViewById(R.id.level);
        	price = (TextView) view.findViewById(R.id.price);
        	description = (TextView) view.findViewById(R.id.description);
        }

    }

}
