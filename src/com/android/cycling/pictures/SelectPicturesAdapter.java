package com.android.cycling.pictures;

import java.util.List;

import com.android.cycling.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class SelectPicturesAdapter extends ArrayAdapter<PictureBean>{
	
	public static interface Listener {
		void onActionUnableSelected(int pisition);
	}
	
	private LayoutInflater mInflater;
	private DisplayImageOptions options;
	
	private Listener mListener;
	
	public void setListener(Listener listener) {
		mListener = listener;
	}
	
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
	
	public void setData(List<PictureBean> data) {
		clear();
		if(data == null) return;
		addAll(data);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final PictureBean bean = getItem(position);
		
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
		viewCache.setPictureBean(bean);
		if(bean.isSelected()) {
			viewCache.picSelect.setImageResource(R.drawable.pictures_selected);
		} else {
			viewCache.picSelect.setImageResource(R.drawable.pictures_unselected);
		}
		
		//Using Android-Universal-Image-Loader do display image
		ImageLoader.getInstance().displayImage(bean.uri, viewCache.pic,
				options, new ImageLoadingListener(viewCache.picSelect, position));
		return result;
	}
	
	private class ImageLoadingListener extends SimpleImageLoadingListener {
		
		private final ImageView mPicSelect;
		private final int mPosition;
		
		public ImageLoadingListener(ImageView picSelect, int position) {
			mPicSelect = picSelect;
			mPosition = position;
		}
		
		private void setVisiblePictureSelect(boolean visible) {
			if(!visible) {
				mPicSelect.setVisibility(View.GONE);
				if (mListener != null) {
					mListener.onActionUnableSelected(mPosition);
				}
			} else {
				mPicSelect.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			setVisiblePictureSelect(false);
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			setVisiblePictureSelect(true);
		}
		
	}
	
	public static class PictureListItemViewCache {
		public final ImageView pic;
		public final ImageView picSelect;
		
		private PictureBean mBean;
		
		public PictureListItemViewCache(View view) {
			pic = (ImageView)view.findViewById(R.id.id_item_image);
			picSelect = (ImageView) view.findViewById(R.id.id_item_select);
		}
		
		public void setPictureBean(PictureBean bean) {
			mBean = bean;
		}
		
		public PictureBean getPictureBean() {
			return mBean;
		}
	}

}
