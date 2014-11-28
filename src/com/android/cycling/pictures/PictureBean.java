package com.android.cycling.pictures;

public final class PictureBean {
	public final String uri;
	private boolean mSelected;
	
	public PictureBean(String uri) {
		this.uri = uri;
		mSelected = false;
	}
	
	public void setSelected(boolean sel) {
		mSelected = sel;
	}
	
	public boolean isSelected() {
		return mSelected;
	}
	
}
