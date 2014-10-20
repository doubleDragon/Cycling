package com.android.cycling.indicator;

import android.graphics.drawable.Drawable;

public class SimpleTab {
	
	public final Drawable icon;
	public final CharSequence text;
	public final int position;
	
	public SimpleTab(Drawable icon, CharSequence text, int position) {
		this.icon = icon;
		this.text = text;
		this.position = position;
	}
	
}
