package com.android.cycling.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.android.cycling.CycingSaveService;

public final class DataUtils {
	
	public static final String FILE_URI_PREFIX = "file://";
	
	public static void syncIssueFromServer(Context context) {
		Intent i = CycingSaveService.createSyncIssueIntent(context);
		context.startService(i);
	}
	
	/**
	 * Convert uri content://media/external/image/media/102 to
	 * file:///xxx
	 * @param context
	 * @param uri
	 * @return
	 */
	public static String convertUri(Context context, Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, proj, null,
				null, null);
		try {
			int actual_image_column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String img_path = cursor.getString(actual_image_column_index);
			return img_path;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

}
