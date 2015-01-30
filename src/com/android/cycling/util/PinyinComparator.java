package com.android.cycling.util;

import java.util.Comparator;

import com.android.cycling.data.ServerUser;

public class PinyinComparator implements Comparator<ServerUser> {

	public int compare(ServerUser o1, ServerUser o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
