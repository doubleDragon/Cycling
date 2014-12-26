package com.android.cycling.secondhand;

import java.util.Iterator;
import java.util.List;

import com.android.cycling.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class IssueListPhotoAdapter extends ArrayAdapter<String>{
	
	private LayoutInflater mInflater;
	private DisplayImageOptions options;
	
	public IssueListPhotoAdapter(Context context) {
		super(context, R.layout.issue_list_photo_list_item);
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
		ensureDataLenght(data);
		addAll(data);
	}
	
	/**
	 *  Max length is 3
	 * 
	 */
	private void ensureDataLenght(List<String> data) {
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
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String uri = getItem(position);
		
		View result;
		IssuePhotoListItemViewCache viewCache;
		if(convertView != null) {
			result = convertView;
			viewCache = (IssuePhotoListItemViewCache)result.getTag();
		} else {
			result = mInflater.inflate(R.layout.issue_list_photo_list_item, parent, false);
			viewCache = new IssuePhotoListItemViewCache(result);
			result.setTag(viewCache);
		}
		
		//Using Android-Universal-Image-Loader do display image
		ImageLoader.getInstance().displayImage(uri, viewCache.pic, options);
		return result;
	}
	
	private static class IssuePhotoListItemViewCache {
		public final ImageView pic;
		
		public IssuePhotoListItemViewCache(View view) {
			pic = (ImageView)view.findViewById(R.id.id_item_image);
		}
		
	}

}
