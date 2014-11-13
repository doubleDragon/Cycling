package com.android.cycling.widget;

import com.android.cycling.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MultiCheck extends LinearLayout implements View.OnClickListener{
	
	private Button mLeftBt;
	private Button mMiddleBt;
	private Button mRightBt;
	
	private static final int LEFT_INDEX = 0;
	private static final int MIDDLE_INDEX = 1;
	private static final int RIGHT_INDEX = 2;
	
	private static final Position mDefaultPosition = Position.LEFT;
	private Position mPosition = mDefaultPosition;
	
	public MultiCheck(Context context) {
		this(context, null);
	}

	public MultiCheck(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		setOrientation(LinearLayout.HORIZONTAL);
		
		mLeftBt = new Button(getContext());
		mMiddleBt = new Button(getContext());
		mRightBt = new Button(getContext());
		
		mLeftBt.setId(LEFT_INDEX);
		mMiddleBt.setId(MIDDLE_INDEX);
		mRightBt.setId(RIGHT_INDEX);
		
		mLeftBt.setText(R.string.bike_all_sell);
		mMiddleBt.setText(R.string.bike_parts_sell);
		mRightBt.setText(R.string.ask_buy);
		
		mLeftBt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		mMiddleBt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		mRightBt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		
		mLeftBt.setBackgroundResource(R.drawable.multicheck_bt);
		mMiddleBt.setBackgroundResource(R.drawable.multicheck_bt);
		mRightBt.setBackgroundResource(R.drawable.multicheck_bt);
		
		mLeftBt.setOnClickListener(this);
		mMiddleBt.setOnClickListener(this);
		mRightBt.setOnClickListener(this);
		
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.weight = 1;
        mLeftBt.setLayoutParams(lp);
        mMiddleBt.setLayoutParams(lp);
        mRightBt.setLayoutParams(lp);
		
		addView(mLeftBt);
		addView(mMiddleBt);
		addView(mRightBt);
		
		updateBt();
	}
	
	private void updateBt() {
		
		switch(mPosition) {
		case LEFT:
			mLeftBt.setSelected(true);
			mMiddleBt.setSelected(false);
			mRightBt.setSelected(false);
			break;
		case MIDDLE:
			mLeftBt.setSelected(false);
			mMiddleBt.setSelected(true);
			mRightBt.setSelected(false);
			break;
		case RIGHT:
			mLeftBt.setSelected(false);
			mMiddleBt.setSelected(false);
			mRightBt.setSelected(true);
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case LEFT_INDEX:
			mPosition = Position.LEFT;
			break;
		case MIDDLE_INDEX:
			mPosition = Position.MIDDLE;
			break;
		case RIGHT_INDEX:
			mPosition = Position.RIGHT;
			break;
		}
		updateBt();
	}
	
	private enum Position {
		LEFT, MIDDLE, RIGHT;
		
		public int getIndex() {
			switch(this) {
			case LEFT:
				return LEFT_INDEX;
			case MIDDLE:
				return MIDDLE_INDEX;
			case RIGHT:
				return RIGHT_INDEX;
			default:
				throw new IllegalArgumentException("Wrong index postion");
			}
		}
	}

}
