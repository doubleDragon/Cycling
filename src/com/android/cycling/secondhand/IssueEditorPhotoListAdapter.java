package com.android.cycling.secondhand;

import java.util.List;

import com.android.cycling.R;
import com.android.cycling.widget.AddPhotoView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class IssueEditorPhotoListAdapter extends ArrayAdapter<String>{
	
	private LayoutInflater mInflater;
	
	private Context mContext;
	private DisplayImageOptions options;
	
	private List<String> mData;

	public IssueEditorPhotoListAdapter(Context context) {
		super(context, R.layout.photo_list_item);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
	}
	
	public void setData(List<String> data) {
		clear();
		if(data == null) return;
		addAll(data);
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
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String uriPath = getItem(position);
		View result;
		PhotoListItemViewCache viewCache;
		if (convertView == null) {
			result = mInflater.inflate(R.layout.photo_list_item, parent, false);
			viewCache = new PhotoListItemViewCache(result);
			result.setTag(viewCache);
		} else {
			result = convertView;
			viewCache = (PhotoListItemViewCache) result.getTag();
		}
		// Using Android-Universal-Image-Loader do display image
		ImageLoader.getInstance().displayImage(uriPath, viewCache.photo,
				options);
		return result;
	}
	
	public static class PhotoListItemViewCache {
		public final ImageView photo;
		
		public PhotoListItemViewCache(View v) {
			photo = (ImageView) v.findViewById(R.id.id_item_image);
		}
	}

}
