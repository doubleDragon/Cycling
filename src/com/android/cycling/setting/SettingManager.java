package com.android.cycling.setting;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

import com.android.cycling.data.ServerUser;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


public class SettingManager {

	public static interface Listener {
		void onComplete(boolean success);
	}
	
	private static final String TAG = SettingManager.class.getSimpleName();
	private static final boolean DEBUG = true;
	
	private static final int STATUS_UPDATE_AVATAR_ONLY = 1;
	private static final int STATUS_UPDATE_GALLERY_ONLY = 2;
	private static final int STATUS_UPDATE_AVATAR_AND_GALLERY = 3;
	
	private int mStatus;
	
	private final Context mContext;
	private Listener mListener;
	
	private boolean mResult;
	
	public void setListener(Listener listener) {
		mListener = listener;
	}

	public SettingManager(Context mContext) {
		this.mContext = mContext;
	}
	
	private void callbackResult() {
		if(mListener != null) {
			mListener.onComplete(mResult);
		}
	}
	
	public void updateUserInfo(ServerUser user, String avatarUriString, String name, String age,
			String sex, String location, String signature, String[] gallery) {
		if(!TextUtils.isEmpty(name)) {
			user.setUsername(name);	
		}
		if(!TextUtils.isEmpty(age)) {
			user.setAge(age);			
		}
		if(!TextUtils.isEmpty(sex)) {
			if(sex.equals("男")) {
				user.setMale(true);
			} else if(sex.equals("女")){
				user.setMale(false);
			}
		}
		if(!TextUtils.isEmpty(location)) {
			user.setLocation(location);
		}
		if(!TextUtils.isEmpty(signature)) {
			user.setSignature(signature);
		}
		
		boolean isNeedUpdateAvatar = false;
		boolean isNeedUpdateGallery = false;
		if(!TextUtils.isEmpty(avatarUriString)) {
			isNeedUpdateAvatar = true;
		}
		if(gallery != null && gallery.length > 0) {
			isNeedUpdateGallery = true;
		}
		
		if(isNeedUpdateAvatar && isNeedUpdateGallery) {
			mStatus = STATUS_UPDATE_AVATAR_AND_GALLERY;
			uploadAvatarAndGallery(avatarUriString, gallery, user);
		} else if(isNeedUpdateAvatar && !isNeedUpdateGallery) {
			mStatus = STATUS_UPDATE_AVATAR_ONLY;
			uploadAvatarAndGallery(avatarUriString, gallery, user);
		} else if(!isNeedUpdateAvatar && isNeedUpdateGallery) {
			mStatus = STATUS_UPDATE_GALLERY_ONLY;
			uploadAvatarAndGallery(avatarUriString, gallery, user);
		} else {
			update(user);
		}

	}
	
	/**
	 * update text information,inclu age sex name and avatar gallery path
	 * @param user
	 */
	private void update(ServerUser user) {
		user.update(mContext, new UpdateListener() {
			
			@Override
			public void onSuccess() {
				logW("update success");
				mResult = true;
				callbackResult();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				logW("update failed---error: " + arg1);
				mResult = false;
				callbackResult();
			}
		});
	}
	
	private String[] generateUploadPathArray(String avatar, String[] gallery) {
		String[] result;
		switch (mStatus) {
		case STATUS_UPDATE_GALLERY_ONLY:
			result = new String[gallery.length];
			System.arraycopy(gallery, 0, result, 0, gallery.length);
			break;
		case STATUS_UPDATE_AVATAR_ONLY:
			result = new String[] {avatar};
			break;
		case STATUS_UPDATE_AVATAR_AND_GALLERY:
			result = new String[gallery.length + 1];
			result[0] = avatar;
			System.arraycopy(gallery, 0, result, 1, gallery.length);
			break;
		default:
			throw new IllegalStateException();
		}
		return result;
	}
	
	private void uploadAvatarAndGallery(String avatar, String[] gallery, ServerUser user) {
		String[] temp = generateUploadPathArray(avatar, gallery);
		Bmob.uploadBatch(mContext, temp, new SettingUploadBatchListener(user, temp.length));
	}
	
	private class SettingUploadBatchListener implements UploadBatchListener {
		
		final ServerUser user;
		final int count;
		
		public SettingUploadBatchListener(ServerUser user, int count) {
			this.user = user;
			this.count = count; 
		}

		@Override
		public void onError(int arg0, String arg1) {
			// TODO Auto-generated method stub
			logW("SettingUploadBatchListener---failed arg1:" + arg1);
			mResult = false;
			callbackResult();
		}

		@Override
		public void onProgress(int arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSuccess(List<BmobFile> arg0, List<String> arg1) {
			logW("SettingUploadBatchListener---onSuccess arg1:" + arg1);
			int size = arg0.size();
			if(size != count) {
				//Is't not last uploaded file,just return
				return;
			}
			int i;
			switch(mStatus) {
			case STATUS_UPDATE_GALLERY_ONLY:
				user.addGalleryPhotos(new ArrayList<String>(arg1));
				break;
			case STATUS_UPDATE_AVATAR_ONLY:
				user.setAvatar(arg1.get(0));
				break;
			case STATUS_UPDATE_AVATAR_AND_GALLERY:
				i = 0;
				for(String temp : arg1) {
					if(i == 0) {
						user.setAvatar(temp);
					} else {
						user.addGalleryPhoto(temp);
					}
					i++;
				}
				break;
			default:
				throw new IllegalStateException();
			}
			
			update(user);
			
		}
		
	}
	
	private static void logW(String msg) {
		if(DEBUG) {
			Log.d(TAG, msg);
		}
	}
	
}
