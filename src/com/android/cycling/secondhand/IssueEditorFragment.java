package com.android.cycling.secondhand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.android.cycling.R;
import com.android.cycling.activities.IssueEditorActivity;
import com.android.cycling.activities.SelectPicturesActivity;
import com.android.cycling.util.DataUtils;
import com.android.cycling.util.DateUtils;
import com.android.cycling.util.NetworkUtils;
import com.android.cycling.widget.HeaderLayout.Action;
import com.android.cycling.widget.MultiCheck;
import com.android.cycling.widget.SimpleGridView;
import com.android.cycling.widget.HeaderLayout;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class IssueEditorFragment extends Fragment {
	
	public static interface Listener {
		void start();
		void stop();
	}
	
	private static final String TAG = IssueEditorFragment.class.getSimpleName();
	
	private final String ADD_PHOTO_URI = "assets://addPhoto.png";
	private static final String FILE_URI_PREFIX = "file://";
	
	private Context mContext;
	
	private String mAction;
	
	private EditText mName;//商品名称
	private EditText mLevel;//新旧程度
	private EditText mPrice;//商品价格
	private EditText mPhone;//联系方式
	private EditText mDescription;//商品描述
	private MultiCheck mType;//交易类型
	private SimpleGridView mPhotoList;//商品图片
	private IssueEditorPhotoListAdapter mAdapter;
	
	private IssueManager mIssueManager;
	
	private ProgressDialog mDisplayDialog;
	
	private IssueEditorFragment.Listener mFragmentListener;
	
	private final IssueManager.Listener mListener = new IssueManager.Listener() {

		@Override
		public void onComplete(boolean success) {
			if(mFragmentListener != null) {
				mFragmentListener.stop();
			}
				
			if (!success) {
				toastMessage("发贴失败");
			} else {
				// 本地保存issue
				toastMessage("发贴成功");
				DataUtils.syncIssueFromServer(mContext);
			}
			finishActivity();
		}
		
	};
	
	public void setListener(IssueEditorFragment.Listener listener) {
		mFragmentListener = listener;
	}
	
	public void load(String action, Uri uri, Bundle extras) {
		mAction = action;
	}
	
	private void startIssueDataLoader() {
		
	}
	
	private void setupEditor() {
		setEmptyData();
	}
	
	private void setEmptyData() {
		List<String> data = new ArrayList<String>();
		data.add(ADD_PHOTO_URI);
		mAdapter.setData(data);
	}
	
	public void setAdapterData(String[] uriPathArray) {
		if(uriPathArray == null || uriPathArray.length < 1) return;
		
		List<String> data = new ArrayList<String>(Arrays.asList(uriPathArray));
		if(data.size() < 6) {
			data.add(ADD_PHOTO_URI);
		}
		mAdapter.setData(data);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null) {
			onRestoreInstanceState(savedInstanceState);
		} else if(Intent.ACTION_EDIT.equals(mAction)) {
			//Load data from database base on mUri
			startIssueDataLoader();
		} else if(Intent.ACTION_INSERT.equals(mAction)) {
			setupEditor();
		} else {
			throw new IllegalArgumentException("Unknow Action String" + mAction +
					". Only support " + Intent.ACTION_EDIT + " or " + 
					Intent.ACTION_INSERT);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIssueManager =  new IssueManager(mContext);
		mIssueManager.setListener(mListener);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}
	
	@Override
	public void onDetach() {
		mContext = null;
		super.onDetach();
	}
	
	private void finishActivity() { 
		getActivity().finish();
	}
	
	private void saveIssue() {
		if(!NetworkUtils.isNetworkConnected(mContext)) {
			toastMessage(R.string.no_network);
			return;
		}
		
		final String name = mName.getEditableText().toString();
		if(TextUtils.isEmpty(name)) {
			toastMessage(R.string.no_issue_name);
			return;
		}
		final String level = mLevel.getEditableText().toString();
		if(TextUtils.isEmpty(level)) {
			toastMessage(R.string.no_issue_level);
			return;
		}
		final String price = mPrice.getEditableText().toString();
		if(TextUtils.isEmpty(price)) {
			toastMessage(R.string.no_issue_price);
			return;
		}
		final String description = mDescription.getEditableText().toString();
		if(TextUtils.isEmpty(description)) {
			toastMessage(R.string.no_issue_description);
			return;
		}
		final String phone = mPhone.getEditableText().toString();
		int type = mType.getType();
		String[] pictures = mAdapter.getAllData();

		if(mFragmentListener != null) {
			mFragmentListener.start();
		}
		mIssueManager.saveIssueToServer(name, level, price, description, phone, type, pictures);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		View root = getView();
		if(root == null) {
			throw new IllegalStateException("Content view not yet created");
		}
		
		HeaderLayout actionBar = (HeaderLayout) root.findViewById(R.id.header_layout);
		// You can also assign the title programmatically by passing a
		// CharSequence or resource id.
		actionBar.setTitle(R.string.issue);
		actionBar.setHomeAction(new Action() {
			
			@Override
			public void performAction(View view) {
				finishActivity();
			}
			
			@Override
			public int getDrawable() {
				// TODO Auto-generated method stub
				return R.drawable.back_indicator;
			}
		});
		actionBar.addAction(new Action() {

			@Override
			public int getDrawable() {
				return R.drawable.confirm_indicator;
			}

			@Override
			public void performAction(View view) {
				saveIssue();
			}
			
		});

		
		mName = (EditText) root.findViewById(R.id.name);
		mLevel = (EditText) root.findViewById(R.id.level);
		mPrice = (EditText) root.findViewById(R.id.price);
		mPhone = (EditText) root.findViewById(R.id.phone);
		mDescription = (EditText) root.findViewById(R.id.description);
		
		mType = (MultiCheck) root.findViewById(R.id.type);
		
		mAdapter = new IssueEditorPhotoListAdapter(mContext);
		
		mPhotoList = (SimpleGridView) root.findViewById(R.id.pictureList);
		mPhotoList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int count = mAdapter.getCount();
				if(count == 1 || position == count) {
					intentToSelectPicture();
				}
			}
			
		});
		mPhotoList.setAdapter(mAdapter);
	}
	
	private void intentToSelectPicture() {
		Intent i =  new Intent(mContext, SelectPicturesActivity.class);
		getActivity().startActivityForResult(i, IssueEditorActivity.REQUEST_SELECT_PICTURE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.issue_editor_fragment, container, false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	private void onRestoreInstanceState(Bundle state) {
		
	}
	
	private void toastMessage(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
	private void toastMessage(int msgId) {
		Toast.makeText(mContext, msgId, Toast.LENGTH_SHORT).show();
	}
	
}
