package com.android.cycling.util;

import java.util.Calendar;

public class DateUtils {
	
	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}
	
	public static String getDisplayTime(long oldTime) {
		long curTime = getCurrentTime();
		if(curTime < oldTime) {
			return null;
		}
		long gapTime = curTime - oldTime;
//		long seconds = gapTime / 1000;
		long minutes = gapTime / (60 * 1000);
		long hours = gapTime / (60 * 60 * 1000);
		if(hours > 24) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(oldTime);
			int month = calendar.get(Calendar.MONTH);
			int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
			return month + "-" + dayOfYear;
		} else if(hours > 1 && hours <= 24) {
			return hours + "小时前";
		} else {
			return minutes + "分钟前";
		}
		
	}

}
