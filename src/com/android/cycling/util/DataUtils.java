package com.android.cycling.util;

import android.content.Context;
import android.content.Intent;

import com.android.cycling.CycingSaveService;

public final class DataUtils {
	
	public static void syncIssueFromServer(Context context) {
		Intent i = CycingSaveService.createSyncIssueIntent(context);
		context.startService(i);
	}

}
