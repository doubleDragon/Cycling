package com.android.cycling.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.cycling.R;

public class BadgeView extends ImageView {

	private Bitmap mTipBitmap;
	private boolean mTipVisible;

	public BadgeView(Context context) {
		this(context, null);
	}

	public BadgeView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BadgeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.BadgeView);
		Drawable drawable = a.getDrawable(R.styleable.BadgeView_tipResource);
		if (drawable == null) {
			drawable = context.getResources().getDrawable(
					R.drawable.bmob_msg_tips);
		}
		mTipBitmap = ((BitmapDrawable) drawable).getBitmap();
		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mTipVisible) {
			drawTip(canvas);
		}
	}

	private void drawTip(Canvas canvas) {
		float left = getMeasuredWidth() - mTipBitmap.getWidth();
		canvas.drawBitmap(mTipBitmap, left, 0, null);
	}

	public void setTipVisible(boolean visible) {
		mTipVisible = visible;
		updateTip();
	}

	private void updateTip() {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			invalidate();
		} else {
			postInvalidate();
		}
	}
}
