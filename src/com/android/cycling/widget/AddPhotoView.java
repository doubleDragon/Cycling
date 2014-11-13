package com.android.cycling.widget;

import com.android.cycling.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddPhotoView extends LinearLayout{
	
	private Paint mPaint;

	//dip
	private float mStrokeWidthPix = 2;
	private int mLabelMarginTop = 8;
	private int mIconMarginTop = 18;
	private int mContentHeightAndWidth=70;
	
	public AddPhotoView(Context context) {
		this(context, null);
	}

	public AddPhotoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}
	
	private void init(Context context) {
		requestLayout();
		
		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		
		mStrokeWidthPix = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mStrokeWidthPix, metrics);
		mLabelMarginTop = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mLabelMarginTop, metrics);
		mIconMarginTop = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mIconMarginTop, metrics);
		mContentHeightAndWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mContentHeightAndWidth, metrics);
		
		mPaint = new Paint();
		mPaint.setColor(Color.rgb(228, 228, 228));
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(mStrokeWidthPix);
		
		LayoutParams labelLp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		labelLp.topMargin = mLabelMarginTop;
		labelLp.gravity = Gravity.CENTER_HORIZONTAL;
		
		TextView label = new TextView(context);
		label.setText(R.string.add_photo);
		label.setTextColor(Color.rgb(177, 177, 177));
		label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		label.setLayoutParams(labelLp);
		
		LayoutParams iconLp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		
		iconLp.topMargin = mIconMarginTop;
		iconLp.gravity = Gravity.CENTER_HORIZONTAL;
		
		ImageView icon = new ImageView(context);
		icon.setImageResource(R.drawable.add);
		icon.setLayoutParams(iconLp);
		
		setOrientation(LinearLayout.VERTICAL);
		setBackgroundColor(Color.WHITE);
		addView(icon,0);
		addView(label,1);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		widthMeasureSpec = heightMeasureSpec = MeasureSpec.makeMeasureSpec(mContentHeightAndWidth, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	
}
