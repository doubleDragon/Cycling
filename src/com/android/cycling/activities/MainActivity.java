package com.android.cycling.activities;

import com.android.cycling.CyclingActivity;
import com.android.cycling.R;
import com.android.cycling.activities.ActionBarAdapter.TabState;
import com.android.cycling.around.AroundFragment;
import com.android.cycling.contacts.ContactsFragment;
import com.android.cycling.messages.MessagesFragment;
import com.android.cycling.secondhand.IssueListFragment;
import com.android.cycling.setting.SettingFragment;
import com.android.cycling.widget.Indicator;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


public class MainActivity extends CyclingActivity {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private Indicator mIndicator;
	private TabPagerAdapter mPagerAdapter;
	private ViewPager mTabPager;
	
	private IssueListFragment mFragment0;
	private MessagesFragment mFragment1;
	private AroundFragment mFragment2;
	private ContactsFragment mFragment3;
	private SettingFragment mFragment4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        createViewsAndFragments();
    }
    
    private void createViewsAndFragments() {
    	final FragmentManager fragmentManager = getFragmentManager();  
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        
    	mPagerAdapter = new TabPagerAdapter();
    	mTabPager = (ViewPager) findViewById(R.id.viewPager);
    	mTabPager.setAdapter(mPagerAdapter);
    	
    	mIndicator = (Indicator) findViewById(R.id.indicator);
    	mIndicator.setViewPager(mTabPager);
        
        final String TAG0 = "tab-pager-index0";  
        final String TAG1 = "tab-pager-index1";
        final String TAG2 = "tab-pager-index2";
        final String TAG3 = "tab-pager-index3";
        final String TAG4 = "tab-pager-index4";
        
    	mFragment0 = (IssueListFragment) fragmentManager  
                .findFragmentByTag(TAG0);
    	mFragment1 = (MessagesFragment) fragmentManager  
                .findFragmentByTag(TAG1);
    	mFragment2 = (AroundFragment) fragmentManager  
                .findFragmentByTag(TAG2);
    	mFragment3 = (ContactsFragment) fragmentManager  
                .findFragmentByTag(TAG3);
    	mFragment4 = (SettingFragment) fragmentManager  
                .findFragmentByTag(TAG4);
    	
    	if(mFragment0 == null) {
    		mFragment0 = new IssueListFragment();
        	mFragment1 = new MessagesFragment();
        	mFragment2 = new AroundFragment();
        	mFragment3 = new ContactsFragment();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Adapter for the {@link ViewPager}.  Unlike {@link FragmentPagerAdapter},
     * {@link #instantiateItem} returns existing fragments, and {@link #instantiateItem}/
     * {@link #destroyItem} show/hide fragments instead of attaching/detaching.
     *
     * In search mode, we always show the "all" fragment, and disable the swipe.  We change the
     * number of items to 1 to disable the swipe.
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
                if(object == mFragment0) {
					return TabState.SECOND_HAND;
				}
				if(object == mFragment1) {
					return TabState.MESSAGES;
				}
				if(object == mFragment2) {
					return TabState.AROUND;
				}
				if(object == mFragment3) {
					return TabState.CONTACTS;
				}
				if(object == mFragment4) {
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
                    Log.w(TAG, "Request fragment at position=" + position + ", eventhough we " +
                            "are in search mode");
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

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
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
