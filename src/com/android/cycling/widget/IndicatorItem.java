package com.android.cycling.widget;

import com.android.cycling.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class IndicatorItem extends View {

	private Bitmap mIconBitmap;
	private Bitmap mBitmapInMemory;
	private int mColor;
	private String mText;
	private float mTextSize;

	private Paint mTextPaint;
	private Rect mTextBound;

	private Rect mIconRect;

	private float mIconAlpha = 0.0f;

	public IndicatorItem(Context context) {
		this(context, null);
	}

	public IndicatorItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public IndicatorItem(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.IndicatorItem);

		BitmapDrawable drawable = (BitmapDrawable) a
				.getDrawable(R.styleable.IndicatorItem_icon);
		mIconBitmap = drawable.getBitmap();
		mColor = a.getColor(R.styleable.IndicatorItem_color, 0xFF009DD9);
		mText = a.getString(R.styleable.IndicatorItem_text);

		float defaultTextSize = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 12, context.getResources()
						.getDisplayMetrics());
		mTextSize = a.getDimension(R.styleable.IndicatorItem_textSize,
				defaultTextSize);

		a.recycle();

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(0xFF8E8E8E);

		mTextBound = new Rect();
		mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);

		mIconRect = new Rect();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int iconWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
				- getPaddingRight(), getMeasuredHeight() - getPaddingTop()
				- getPaddingBottom() - mTextBound.height());

		int left = getMeasuredWidth() / 2 - iconWidth / 2;
		int top = (getMeasuredHeight() - iconWidth - mTextBound.height()) / 2;
		mIconRect.set(left, top, left + iconWidth, top + iconWidth);// icon可绘制的区域，跟bitmap的宽高要区分
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawSourceBitmap(canvas);

		int alpha = (int) Math.ceil(255 * mIconAlpha);
		setBimtmapInMemory(alpha);
		drawSouceText(canvas, alpha);
		drawTargetText(canvas, alpha);
		drawTargetBitmap(canvas);
	}

	private void drawTargetBitmap(Canvas canvas) {
		canvas.drawBitmap(mBitmapInMemory, 0, 0, null);
	}

	private void setBimtmapInMemory(int alpha) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(mColor);
		paint.setAlpha(alpha);
		
		mBitmapInMemory = Bitmap.createBitmap(getMeasuredWidth(),
				getMeasuredHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmapInMemory);
		canvas.drawRect(mIconRect, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		paint.setAlpha(255);

		canvas.drawBitmap(mIconBitmap, null, mIconRect, paint);
	}

	private void drawSourceBitmap(Canvas convas) {
		convas.drawBitmap(mIconBitmap, null, mIconRect, null);
	}

	private void drawSouceText(Canvas convas, int alpha) {
		mTextPaint.setColor(0xFF8E8E8E);
		mTextPaint.setAlpha(255 - alpha);
		float x = (getMeasuredWidth() - mTextBound.width()) / 2;
		float y = mIconRect.bottom + mTextBound.height();
		convas.drawText(mText, x, y, mTextPaint);

	}

	private void drawTargetText(Canvas convas, int alpha) {
		mTextPaint.setColor(mColor);
		mTextPaint.setAlpha(alpha);
		float x = (getMeasuredWidth() - mTextBound.width()) / 2;
		float y = mIconRect.bottom + mTextBound.height();
		convas.drawText(mText, x, y, mTextPaint);
	}

	public void setIconAlpha(float alpha) {
		mIconAlpha = alpha;
		invalidateView();
	}

	private void invalidateView() {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			invalidate();
		} else {
			postInvalidate();
		}
	}
	
	private static final String INSTANCE_STATUS = "instance_status";
	private static final String STATUS_ALPHA = "status_alpha";

	@Override
	protected Parcelable onSaveInstanceState()
	{
		Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
		bundle.putFloat(STATUS_ALPHA, mIconAlpha);
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		if (state instanceof Bundle)
		{
			Bundle bundle = (Bundle) state;
			mIconAlpha = bundle.getFloat(STATUS_ALPHA);
			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
			return;
		}
		super.onRestoreInstanceState(state);
	}

}
