package com.android.cycling.indicator;

import com.android.cycling.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

public class IndicatorPolicy {
	
	private static final int DEFAULT_TAB_CONTAINER_HEIGHT = 58;//DIP
	private static final int TAB_TOP_PADDING = 8;//DIP
	
	private Context mContext;

	public static IndicatorPolicy get(Context context) {
        return new IndicatorPolicy(context);
    }

    private IndicatorPolicy(Context context) {
        mContext = context;
    }
    
    public int getTabContainerHeight() {
//        TypedArray a = mContext.obtainStyledAttributes(null, R.styleable.Indicator,
//                R.attr.indicatorStyle, 0);
//        int height = a.getLayoutDimension(R.styleable.Indicator_height, 0);
//        a.recycle();
//        return height; 
        int result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TAB_CONTAINER_HEIGHT, mContext.getResources().getDisplayMetrics());
        return result;
    }
    
    public int getTabTopAndBottomPadding() {
    	int result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TAB_TOP_PADDING, mContext.getResources().getDisplayMetrics());
    	return result;
    }
    
    public int getStackedTabMaxWidth() {
        return mContext.getResources().getDimensionPixelSize(
                R.dimen.indicator_stacked_tab_max_width);
    }
}
