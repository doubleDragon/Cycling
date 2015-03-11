package com.android.cycling.util;

import android.graphics.Bitmap;

import com.android.cycling.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImageLoadOptions {

	public static DisplayImageOptions getOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.bmob_head)
				.showImageForEmptyUri(R.drawable.bmob_head)
				.showImageOnFail(R.drawable.bmob_head).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.resetViewBeforeLoading(true)
				.displayer(new FadeInBitmapDisplayer(100)).build();

		return options;
	}

}
