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
import android.widget.ImageButton;
import android.widget.ImageView;

public class SelectPicturesAdapter extends ArrayAdapter<String>{
	
	private LayoutInflater mInflater;
	DisplayImageOptions options;
	
	public SelectPicturesAdapter(Context context) {
		super(context, R.layout.select_pictures_item);
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
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String uri = getItem(position);
		
		View result;
		PictureListItemViewCache viewCache;
		if(convertView != null) {
			result = convertView;
			viewCache = (PictureListItemViewCache)result.getTag();
		} else {
			result = mInflater.inflate(R.layout.select_pictures_item, parent, false);
			viewCache = new PictureListItemViewCache(result);
			result.setTag(viewCache);
		}
		//Using Android-Universal-Image-Loader do display image
		ImageLoader.getInstance().displayImage(uri, viewCache.mPic, options);
		return result;
	}
	
	public static class PictureListItemViewCache {
		public final ImageView mPic;
		public final ImageButton mPicSelect;
		
		public PictureListItemViewCache(View view) {
			mPic = (ImageView)view.findViewById(R.id.id_item_image);
			mPicSelect = (ImageButton) view.findViewById(R.id.id_item_select);
		}
	}

}
