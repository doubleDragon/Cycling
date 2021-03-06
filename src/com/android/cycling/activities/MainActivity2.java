package com.android.cycling.activities;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import cn.bmob.im.BmobChat;

import com.android.cycling.CyclingActivity;
import com.android.cycling.R;
import com.android.cycling.activities.ActionBarAdapter.TabState;
import com.android.cycling.around.AroundFragment;
import com.android.cycling.contacts.ContactListFragment;
import com.android.cycling.messages.MessagesFragment;
import com.android.cycling.secondhand.IssueListFragment;
import com.android.cycling.setting.SettingFragment;
import com.android.cycling.widget.IndicatorItem;

public class MainActivity2 extends CyclingActivity implements
		View.OnClickListener, OnPageChangeListener {

	private static final String TAG = "MainActivity2";

	private List<IndicatorItem> mTabIndicators = new ArrayList<IndicatorItem>();
	private ViewPager mViewPager;
	private TabPagerAdapter mPagerAdapter;

	private Fragment mFragment0;
	private Fragment mFragment1;
	private Fragment mFragment2;
	private Fragment mFragment3;
	private Fragment mFragment4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);

		initView();
		configFragments();

		// 开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
		BmobChat.getInstance(this).startPollService(30);
	}

	@Override
	protected void onDestroy() {
		BmobChat.getInstance(this).stopPollService();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	private void initView() {
		mPagerAdapter = new TabPagerAdapter();
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setAdapter(mPagerAdapter);

		IndicatorItem one = (IndicatorItem) findViewById(R.id.tab0);
		mTabIndicators.add(one);
		IndicatorItem two = (IndicatorItem) findViewById(R.id.tab1);
		mTabIndicators.add(two);
		IndicatorItem three = (IndicatorItem) findViewById(R.id.tab2);
		mTabIndicators.add(three);
		IndicatorItem four = (IndicatorItem) findViewById(R.id.tab3);
		mTabIndicators.add(four);
		IndicatorItem five = (IndicatorItem) findViewById(R.id.tab4);
		mTabIndicators.add(five);

		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);
		four.setOnClickListener(this);
		five.setOnClickListener(this);

		one.setIconAlpha(1.0f);
	}

	private void configFragments() {
		final String TAG0 = "tab-pager-index0";
		final String TAG1 = "tab-pager-index1";
		final String TAG2 = "tab-pager-index2";
		final String TAG3 = "tab-pager-index3";
		final String TAG4 = "tab-pager-index4";

		final FragmentManager fragmentManager = getFragmentManager();
		final FragmentTransaction transaction = fragmentManager
				.beginTransaction();

		mFragment0 = fragmentManager.findFragmentByTag(TAG0);
		mFragment1 = fragmentManager.findFragmentByTag(TAG1);
		mFragment2 = fragmentManager.findFragmentByTag(TAG2);
		mFragment3 = fragmentManager.findFragmentByTag(TAG3);
		mFragment4 = fragmentManager.findFragmentByTag(TAG4);

		if (mFragment0 == null) {
			mFragment0 = new IssueListFragment();
			mFragment1 = new MessagesFragment();
			mFragment2 = new AroundFragment();
			mFragment3 = new ContactListFragment();
			mFragment4 = new SettingFragment();

			transaction.add(R.id.viewPager, mFragment0, TAG0);
			transaction.add(R.id.viewPager, mFragment1, TAG1);
			transaction.add(R.id.viewPager, mFragment2, TAG2);
			transaction.add(R.id.viewPager, mFragment3, TAG3);
			transaction.add(R.id.viewPager, mFragment4, TAG4);
		}

		transaction.hide(mFragment0);
		transaction.hide(mFragment1);
		transaction.hide(mFragment2);
		transaction.hide(mFragment3);
		transaction.hide(mFragment4);

		transaction.commitAllowingStateLoss();
		fragmentManager.executePendingTransactions();

	}

	@Override
	public void onClick(View v) {
		clickTab(v);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if (positionOffset > 0) {
			IndicatorItem left = mTabIndicators.get(position);
			IndicatorItem right = mTabIndicators.get(position + 1);
			left.setIconAlpha(1 - positionOffset);
			right.setIconAlpha(positionOffset);
		}
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 点击Tab按钮
	 * 
	 * @param v
	 */
	private void clickTab(View v) {
		resetOtherTabs();

		switch (v.getId()) {
		case R.id.tab0:
			mTabIndicators.get(0).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(0, false);
			break;
		case R.id.tab1:
			mTabIndicators.get(1).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(1, false);
			break;
		case R.id.tab2:
			mTabIndicators.get(2).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(2, false);
			break;
		case R.id.tab3:
			mTabIndicators.get(3).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(3, false);
			break;
		case R.id.tab4:
			mTabIndicators.get(4).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(4, false);
			break;
		}
	}

	/**
	 * 重置其他的TabIndicator的颜色
	 */
	private void resetOtherTabs() {
		for (int i = 0; i < mTabIndicators.size(); i++) {
			mTabIndicators.get(i).setIconAlpha(0);
		}
	}

	/**
	 * Adapter for the {@link ViewPager}. Unlike {@link FragmentPagerAdapter},
	 * {@link #instantiateItem} returns existing fragments, and
	 * {@link #instantiateItem}/ {@link #destroyItem} show/hide fragments
	 * instead of attaching/detaching.
	 * 
	 * In search mode, we always show the "all" fragment, and disable the swipe.
	 * We change the number of items to 1 to disable the swipe.
	 * 
	 * TODO figure out a more straight way to disable swipe.
	 */
	private class TabPagerAdapter extends PagerAdapter {
		private final FragmentManager mFragmentManager;
		private FragmentTransaction mCurTransaction = null;

		private boolean mTabPagerAdapterSearchMode;

		private Fragment mCurrentPrimaryItem;

		public TabPagerAdapter() {
			mFragmentManager = getFragmentManager();
		}

		public boolean isSearchMode() {
			return mTabPagerAdapterSearchMode;
		}

		public void setSearchMode(boolean searchMode) {
			if (searchMode == mTabPagerAdapterSearchMode) {
				return;
			}
			mTabPagerAdapterSearchMode = searchMode;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabPagerAdapterSearchMode ? 1 : TabState.COUNT;
		}

		/** Gets called when the number of items changes. */
		@Override
		public int getItemPosition(Object object) {
			if (mTabPagerAdapterSearchMode) {
				if (object == mFragment3) {
					return 0; // Only 1 page in search mode
				}
			} else {
				if (object == mFragment0) {
					return TabState.SECOND_HAND;
				}
				if (object == mFragment1) {
					return TabState.MESSAGES;
				}
				if (object == mFragment2) {
					return TabState.AROUND;
				}
				if (object == mFragment3) {
					return TabState.CONTACTS;
				}
				if (object == mFragment4) {
					return TabState.SETTINGS;
				}
			}
			return POSITION_NONE;
		}

		@Override
		public void startUpdate(ViewGroup container) {
		}

		private Fragment getFragment(int position) {
			if (mTabPagerAdapterSearchMode) {
				if (position != 0) {
					// This has only been observed in monkey tests.
					// Let's log this issue, but not crash
					Log.w(TAG, "Request fragment at position=" + position
							+ ", eventhough we " + "are in search mode");
				}
				return mFragment3;
			} else {
				if (position == TabState.SECOND_HAND) {
					return mFragment0;
				} else if (position == TabState.MESSAGES) {
					return mFragment1;
				} else if (position == TabState.AROUND) {
					return mFragment2;
				} else if (position == TabState.CONTACTS) {
					return mFragment3;
				} else if (position == TabState.SETTINGS) {
					return mFragment4;
				}
			}
			throw new IllegalArgumentException("position: " + position);
		}

		@SuppressLint("NewApi")
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if (mCurTransaction == null) {
				mCurTransaction = mFragmentManager.beginTransaction();
			}
			Fragment f = getFragment(position);
			mCurTransaction.show(f);

			// Non primary pages are not visible.
			f.setUserVisibleHint(f == mCurrentPrimaryItem);
			return f;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			if (mCurTransaction == null) {
				mCurTransaction = mFragmentManager.beginTransaction();
			}
			mCurTransaction.hide((Fragment) object);
		}

		@Override
		public void finishUpdate(ViewGroup container) {
			if (mCurTransaction != null) {
				mCurTransaction.commitAllowingStateLoss();
				mCurTransaction = null;
				mFragmentManager.executePendingTransactions();
			}
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return ((Fragment) object).getView() == view;
		}

		@SuppressLint("NewApi")
		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			Fragment fragment = (Fragment) object;
			if (mCurrentPrimaryItem != fragment) {
				if (mCurrentPrimaryItem != null) {
					mCurrentPrimaryItem.setUserVisibleHint(false);
				}
				if (fragment != null) {
					fragment.setUserVisibleHint(true);
				}
				mCurrentPrimaryItem = fragment;
			}
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}
	}

}
