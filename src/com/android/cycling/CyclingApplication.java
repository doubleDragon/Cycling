package com.android.cycling;

import java.io.File;

import cn.bmob.im.BmobChat;
import cn.bmob.v3.Bmob;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Application;
import android.content.Context;

public class CyclingApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		
		initImageLoader(getApplicationContext());
		Bmob.initialize(this, CyclingConfig.BMOB_APPLICATION_ID);
		BmobChat.getInstance(getApplicationContext()).init(CyclingConfig.BMOB_APPLICATION_ID);
	}
	
	public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "imageloader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

}
