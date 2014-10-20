package com.android.cycling.widget;

import com.android.cycling.R;
import com.android.cycling.indicator.IndicatorPolicy;
import com.android.cycling.indicator.SimpleTab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Indicator extends HorizontalScrollView implements OnPageChangeListener{
	
	private int mContentHeight;
	private int mMaxTabWidth;
	private int mSelectedTabIndex;
	private int mStackedTabMaxWidth;
	
	private LinearLayout mTabLayout;
	
	private Runnable mTabSelector;
	private int mSelectedIndex;
	private int mCurrentIndex;
	
	private TabClickListener mTabClickListener;
	private OnPageChangeListener mListener;
	
	private ViewPager mViewPager;
	
	private static void log(String msg) {
		android.util.Log.d("test", msg);
	}
	
	public Indicator(Context context) {
		this(context, null);
	}
	
	public Indicator(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public Indicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		setHorizontalScrollBarEnabled(false);
		setBackgroundResource(R.drawable.indicator_bg);

		IndicatorPolicy abp = IndicatorPolicy.get(context);
		setContentHeight(abp.getTabContainerHeight());
		mStackedTabMaxWidth = abp.getStackedTabMaxWidth();

		mTabLayout = createTabLayout();
		addView(mTabLayout, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
	}
	
	public void setViewPager(ViewPager view) {
		if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        PagerAdapter adapter = view.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        view.setOnPageChangeListener(this);
        notifyDataSetChanged();
	}
	
	public void notifyDataSetChanged() {
        mTabLayout.removeAllViews();
        
        Resources res = getResources();
        int count = res.getInteger(R.integer.indicator_tab_count);
        TypedArray indicatorIcons = res.obtainTypedArray(R.array.indicator_icon);
        TypedArray indicatorText = res.obtainTypedArray(R.array.indicator_text);
        
        boolean setSelected = true;
        for (int i = 0; i < count; i++) {
        	if(i != 0 ) {
        		setSelected = false;
        	}
        	addTab(new SimpleTab(indicatorIcons.getDrawable(i), indicatorText.getString(i), i), setSelected);
        }
        indicatorIcons.recycle();
        indicatorText.recycle();
        
        if (mSelectedIndex > count) {
            mSelectedIndex = count - 1;
        }
        setCurrentItem(mSelectedIndex);
        requestLayout();
    }
	
	public void setOnPageChangeListener(OnPageChangeListener listener) {
        mListener = listener;
    }
	
	@Override
    public void onPageScrollStateChanged(int arg0) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(arg0);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (mListener != null) {
            mListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
    	setCurrentItem(arg0);
        if (mListener != null) {
            mListener.onPageSelected(arg0);
        }
    }
	
	@Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            // Re-post the selector we saved
            post(mTabSelector);
        }
    }
	
	@Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }
	
	public void setContentHeight(int contentHeight) {
        mContentHeight = contentHeight;
        log("setContentHeight: " + contentHeight);
        requestLayout();
    }
	
	private LinearLayout createTabLayout() {
        final LinearLayout tabLayout = new LinearLayout(getContext(), null,
                R.attr.indicatorTabBarStyle);
        tabLayout.setMeasureWithLargestChildEnabled(true);
        tabLayout.setGravity(Gravity.CENTER);
        tabLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        return tabLayout;
    }
	
	public void updateTab(int position) {
        ((TabView) mTabLayout.getChildAt(position)).update();
    }
	
	public void addTab(SimpleTab tab, boolean setSelected) {
        TabView tabView = createTabView(tab);
        mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0,
                LayoutParams.MATCH_PARENT, 1));
        if (setSelected) {
            tabView.setSelected(true);
        }
    }
	
	private TabView createTabView(SimpleTab tab) {
		final TabView tabView = new TabView(getContext(), tab);
		tabView.setFocusable(true);

		if (mTabClickListener == null) {
			mTabClickListener = new TabClickListener();
		}
		tabView.setOnClickListener(mTabClickListener);
		return tabView;
    }
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
		setFillViewport(lockedExpanded);
		
		final int childCount = mTabLayout.getChildCount();
		if(childCount > 1 && 
				(widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
			if (childCount > 2) {
                mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
            } else {
                mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
            }
            mMaxTabWidth = Math.min(mMaxTabWidth, mStackedTabMaxWidth);
		} else {
			mMaxTabWidth = -1;
		}
		
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(mContentHeight, MeasureSpec.EXACTLY);
		
		final int oldWidth = getMeasuredWidth();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int newWidth = getMeasuredWidth();
		
		if(lockedExpanded && oldWidth != newWidth) {
			// Recenter the tab display if we're at a new (scrollable) size.
			setTabSelected(mSelectedTabIndex);
		}
	}
	
	public void setCurrentItem(int index) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        if(mCurrentIndex == index) {
        	//same position tab click twice
        }
        mSelectedIndex = index;
        mViewPager.setCurrentItem(index);

        setTabSelected(index);
    }
	
	public void setTabSelected(int position) {
        mSelectedTabIndex = position;
        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = i == position;
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(position);
            }
        }
    }
	
	public void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }
	
	private class TabView extends LinearLayout {
		
		private SimpleTab mTab;
		private TextView mTextView;
        private ImageView mIconView;
        
		public TabView(Context context, SimpleTab tab) {
			super(context, null, R.attr.indicatorBarTabStyle);
			mTab = tab;
			
			setOrientation(LinearLayout.VERTICAL);
			
			int paddingTop = IndicatorPolicy.get(getContext()).getTabTopAndBottomPadding();
			setPadding(0, paddingTop, 0, 0);
			
			update();
		}
		
		@Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            // Re-measure if we went beyond our maximum size.
            if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth, MeasureSpec.EXACTLY),
                        heightMeasureSpec);
            }
        }
        
		public void update() {
			final Drawable icon = mTab.icon;
			final CharSequence text = mTab.text;
			
			if (icon != null) {
                if (mIconView == null) {
                    ImageView iconView = new ImageView(getContext());
                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
//                    lp.gravity = Gravity.CENTER_VERTICAL;
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
                    iconView.setLayoutParams(lp);
                    addView(iconView, 0);
                    mIconView = iconView;
                }
                mIconView.setImageDrawable(icon);
                mIconView.setVisibility(VISIBLE);
            } else if (mIconView != null) {
                mIconView.setVisibility(GONE);
                mIconView.setImageDrawable(null);
            }
			
			final boolean hasText = !TextUtils.isEmpty(text);
            if (hasText) {
            	
                if (mTextView == null) {
                    TextView textView = new TextView(getContext(), null,
                            R.attr.indicatorBarTabTextStyle);

                    /// M: Long string setting
                    //textView.setEllipsize(TruncateAt.END);
                    textView.setSingleLine(true);
                    textView.setEllipsize(TruncateAt.MARQUEE);
                    textView.setMarqueeRepeatLimit(2);
                    textView.setHorizontalFadingEdgeEnabled(true);
                    
                    ColorStateList csl = (ColorStateList) getContext().getResources().getColorStateList(R.drawable.tab_textview_selector);
                    textView.setTextColor(csl);

                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
//                    lp.gravity = Gravity.CENTER_VERTICAL;
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
                    textView.setLayoutParams(lp);
                    addView(textView);
                    mTextView = textView;
                }
                mTextView.setText(text);
                mTextView.setVisibility(VISIBLE);
            } else if (mTextView != null) {
                mTextView.setVisibility(GONE);
                mTextView.setText(null);
            }
		}
		
		public SimpleTab getTab() {
            return mTab;
        }
	}
	
	private class TabClickListener implements OnClickListener {
        public void onClick(View view) {
            TabView tabView = (TabView) view;
//            tabView.getTab().select();
            mViewPager.setCurrentItem(tabView.getTab().position, true);
            final int tabCount = mTabLayout.getChildCount();
            for (int i = 0; i < tabCount; i++) {
                final View child = mTabLayout.getChildAt(i);
                child.setSelected(child == view);
            }
        }
    }
}
