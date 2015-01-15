package com.android.cycling.secondhand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.android.cycling.CycingSaveService;
import com.android.cycling.R;
import com.android.cycling.activities.IssueEditorActivity;
import com.android.cycling.activities.SelectPicturesActivity;
import com.android.cycling.data.ServerIssue;
import com.android.cycling.secondhand.IssueManager.CallBackObj;
import com.android.cycling.secondhand.IssueManager.SaveIssueResult;
import com.android.cycling.util.DateUtils;
import com.android.cycling.util.NetworkUtils;
import com.android.cycling.widget.MultiCheck;
import com.android.cycling.widget.SimpleGridView;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class IssueEditorFragment extends Fragment {
	
	private static final String TAG = IssueEditorFragment.class.getSimpleName();
	
	private final String ADD_PHOTO_URI = "assets://addPhoto.png";
	private static final String FILE_URI_PREFIX = "file://";
	
	private ContentResolver mContentResolver;
	private Context mContext;
	
	private String mAction;
	private Uri mUri;
	
	private EditText mName;//商品名称
	private EditText mLevel;//新旧程度
	private EditText mPrice;//商品价格
	private EditText mPhone;//联系方式
	private EditText mDescription;//商品描述
	private Button mBack;
	private ImageView mConfirm;
	private MultiCheck mType;//交易类型
	private SimpleGridView mPhotoList;//商品图片
	private IssueEditorPhotoListAdapter mAdapter;
	
	private IssueManager mIssueManager;
	
	private ProgressDialog mDisplayDialog;
	
	private final IssueManager.Listener mListener = new IssueManager.Listener() {

		@Override
		public void onComplete(SaveIssueResult callBackObj) {
			stopDialog();
			if (!callBackObj.isSuccess()) {
				toastMessage("发贴失败");
			} else {
				// 本地保存issue
				toastMessage("发贴成功");
				// Add prefiex to file path
//				addPrefiexToPicturePath(callBackObj.pictures);
				ServerIssue issue = callBackObj.serverIssue;//save web photo path
				android.util.Log.d(TAG, "onComplete serverIssue: " + issue);
				
				List<String> webPathList = callBackObj.getPictureWebPathList();
				String[] pictures;
				if(hasPictures(webPathList)) {
					pictures = webPathList.toArray(new String[0]);
				} else {
					pictures = null;
				}
				
				Intent service = CycingSaveService.createSaveIssueIntent(
						mContext, issue.getName(), issue.getLevel(),
						issue.getPrice(), issue.getDescription(),
						issue.getDate(), issue.getPhone(), issue.getType(),
						issue.getObjectId(), pictures);
				mContext.startService(service);
			}
			finishActivity();
		}
		
	};
	
	private boolean hasPictures(List<String> webPathList) {
		if(webPathList == null || webPathList.isEmpty()) {
			return false;
		}
		return true;
	}
	
	private void addPrefiexToPicturePath(String[] pictures) {
		for(int i=0; i<pictures.length; i++) {
			pictures[i] = FILE_URI_PREFIX + pictures[i];
		}
	}
	
	public void setContentResolver(ContentResolver contentResolver) {
		mContentResolver = contentResolver;
	}
	
	public void load(String action, Uri uri, Bundle extras) {
		mAction = action;
		mUri = uri;
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
		long date = DateUtils.getCurrentTime();

		startDialog();
		mIssueManager.saveIssueToServer(name, level, price, description, date, phone, type, pictures);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		View root = getView();
		if(root == null) {
			throw new IllegalStateException("Content view not yet created");
		}
		
		mConfirm  = (ImageView) root.findViewById(R.id.confirm);
		mConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveIssue();
			}
			
		});
		
		mBack = (Button) root.findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishActivity();
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
	
	private void startDialog() {
		if(mDisplayDialog != null) {
			mDisplayDialog.cancel();
			mDisplayDialog = null;
		}
		mDisplayDialog = new ProgressDialog(mContext);
		mDisplayDialog.setCancelable(false);
		mDisplayDialog.setCanceledOnTouchOutside(false);
		mDisplayDialog.setMessage(mContext.getResources().getString(R.string.sending));
		mDisplayDialog.show();
	}
	
	private void stopDialog() {
		if (mDisplayDialog != null) {
			mDisplayDialog.dismiss();
		}
	}
	
	private void toastMessage(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
	private void toastMessage(int msgId) {
		Toast.makeText(mContext, msgId, Toast.LENGTH_SHORT).show();
	}
	
}
