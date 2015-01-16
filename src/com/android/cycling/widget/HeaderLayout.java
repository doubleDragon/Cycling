package com.android.cycling.widget;

import com.android.cycling.R;

import java.util.LinkedList;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class HeaderLayout extends RelativeLayout implements OnClickListener{
	
	private LayoutInflater mInflater;
	
	private View mRootView;
	private ImageButton mHomeBtn;
	private RelativeLayout mHomeLayout;
	private TextView mTitleView;
	private ImageView mLogoView;
    private View mBackIndicator;
    private LinearLayout mActionsView;

	public HeaderLayout(Context context) {
		this(context, null);
	}

	public HeaderLayout(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public HeaderLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView =  mInflater.inflate(R.layout.header_layout, null);
		addView(mRootView);
		
		mLogoView = (ImageView) mRootView.findViewById(R.id.headerlayout_home_logo);
		mHomeLayout = (RelativeLayout) mRootView.findViewById(R.id.headerlayout_home_bg);
		mHomeBtn = (ImageButton) mRootView.findViewById(R.id.headerlayout_home_btn);
		mBackIndicator = mRootView.findViewById(R.id.headerlayout_home_is_back);
		
		mTitleView = (TextView) mRootView.findViewById(R.id.title);
		mActionsView = (LinearLayout) mRootView.findViewById(R.id.headerlayout_actions);
		
	}
	
	public void setHomeAction(Action action) {
        mHomeBtn.setOnClickListener(this);
        mHomeBtn.setTag(action);
        mHomeBtn.setImageResource(action.getDrawable());
        mHomeLayout.setVisibility(View.VISIBLE);
    }

    public void clearHomeAction() {
        mHomeLayout.setVisibility(View.GONE);
    }
	
	public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    public void setTitle(int resid) {
        mTitleView.setText(resid);
    }
    
    /**
     * Function to set a click listener for Title TextView
     * 
     * @param listener the onClickListener
     */
    public void setOnTitleClickListener(OnClickListener listener) {
        mTitleView.setOnClickListener(listener);
    }

	@Override
	public void onClick(View view) {
		final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction(view);
        }
	}
	
    /**
     * Adds a list of {@link Action}s.
     * @param actionList the actions to add
     */
    public void addActions(ActionList actionList) {
        int actions = actionList.size();
        for (int i = 0; i < actions; i++) {
            addAction(actionList.get(i));
        }
    }

    /**
     * Adds a new {@link Action}.
     * @param action the action to add
     */
    public void addAction(Action action) {
        final int index = mActionsView.getChildCount();
        addAction(action, index);
    }

    /**
     * Adds a new {@link Action} at the specified index.
     * @param action the action to add
     * @param index the position at which to add the action
     */
    public void addAction(Action action, int index) {
        mActionsView.addView(inflateAction(action), index);
    }

    /**
     * Removes all action views from this action bar
     */
    public void removeAllActions() {
        mActionsView.removeAllViews();
    }

    /**
     * Remove a action from the action bar.
     * @param index position of action to remove
     */
    public void removeActionAt(int index) {
        mActionsView.removeViewAt(index);
    }

    /**
     * Remove a action from the action bar.
     * @param action The action to remove
     */
    public void removeAction(Action action) {
        int childCount = mActionsView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mActionsView.getChildAt(i);
            if (view != null) {
                final Object tag = view.getTag();
                if (tag instanceof Action && tag.equals(action)) {
                    mActionsView.removeView(view);
                }
            }
        }
    }
    
    /**
     * Returns the number of actions currently registered with the action bar.
     * @return action count
     */
    public int getActionCount() {
        return mActionsView.getChildCount();
    }

    /**
     * Inflates a {@link View} with the given {@link Action}.
     * @param action the action to inflate
     * @return a view
     */
    private View inflateAction(Action action) {
        View view = mInflater.inflate(R.layout.headerlayout_item, mActionsView, false);

        ImageButton labelView =
            (ImageButton) view.findViewById(R.id.headerlayout_item);
        labelView.setImageResource(action.getDrawable());

        view.setTag(action);
        view.setOnClickListener(this);
        return view;
    }
    
    /**
     * A {@link LinkedList} that holds a list of {@link Action}s.
     */
    public static class ActionList extends LinkedList<Action> {
    }

    /**
     * Definition of an action that could be performed, along with a icon to
     * show.
     */
    public interface Action {
        public int getDrawable();
        public void performAction(View view);
    }

    public static abstract class AbstractAction implements Action {
        final private int mDrawable;

        public AbstractAction(int drawable) {
            mDrawable = drawable;
        }

        @Override
        public int getDrawable() {
            return mDrawable;
        }
    }
    
//    public static class IntentAction extends AbstractAction {
//        private Context mContext;
//        private Intent mIntent;
//
//        public IntentAction(Context context, Intent intent, int drawable) {
//            super(drawable);
//            mContext = context;
//            mIntent = intent;
//        }
//
//        @Override
//        public void performAction(View view) {
//            try {
//               mContext.startActivity(mIntent); 
//            } catch (ActivityNotFoundException e) {
//                Toast.makeText(mContext,
//                        mContext.getText(R.string.actionbar_activity_not_found),
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

}
