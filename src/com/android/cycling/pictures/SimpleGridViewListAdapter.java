package com.android.cycling.pictures;

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

/**
 * GridView list item layout imageview id must be id_item_image 
 * @author wsl
 *
 */
public class SimpleGridViewListAdapter extends ArrayAdapter<String>{
	
	private LayoutInflater mInflater;
	private DisplayImageOptions options;
	
	private int mItemLayoutRes;

	public SimpleGridViewListAdapter(Context context, int resource) {
		super(context, resource);
		mItemLayoutRes = resource;
		mInflater = LayoutInflater.from(context);
		
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}
	
	public void setData(List<String> data) {
		clear();
		if(data == null) return;
		ensureDataLenght(data);
		addAll(data);
		
		notifyDataSetChanged();
	}
	
	/**
	 *  set Max size in horizontal
	 */
	protected void ensureDataLenght(List<String> data) {
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
			result = mInflater.inflate(mItemLayoutRes, parent, false);
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
