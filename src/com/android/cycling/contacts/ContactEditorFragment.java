package com.android.cycling.contacts;

import com.android.cycling.R;
import com.android.cycling.data.ServerUser;
import com.android.cycling.widget.HeaderLayout;
import com.android.cycling.widget.RoundedImageView;
import com.android.cycling.widget.SimpleGridView;
import com.android.cycling.widget.HeaderLayout.Action;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactEditorFragment extends Fragment{
	
	private Context mContext;
	
	private RoundedImageView mAvatar;
	private TextView mName;
	private TextView mAge;
	private TextView mLocation;
	private TextView mSignature;
	private ImageView mSex;
	private SimpleGridView mPhotos;
	private ContactEditorPhotoListAdapter mPhotoAdapter;
	
	private ServerUser mUser;
	
	private DisplayImageOptions options;
	
	public void setServerUser(ServerUser user) {
		mUser = user;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.contact_editor_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		View root = getView();
		if(root == null) {
			throw new IllegalStateException("Content view not yet created");
		}
		HeaderLayout headerLayout = (HeaderLayout) root.findViewById(R.id.header_layout);
		headerLayout.setHomeAction(new Action() {

			@Override
			public int getDrawable() {
				return R.drawable.back_indicator;
			}

			@Override
			public void performAction(View view) {
				getActivity().finish();
			}
			
		});
		
		mAvatar = (RoundedImageView) root.findViewById(R.id.avatar);
		mName = (TextView) root.findViewById(R.id.name);
		mAge = (TextView) root.findViewById(R.id.age);
		mLocation = (TextView) root.findViewById(R.id.location);
		mSex = (ImageView) root.findViewById(R.id.sex);
		mSignature = (TextView) root.findViewById(R.id.signatue_content);
		
		mPhotos = (SimpleGridView) root.findViewById(R.id.photos);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null) {
			onRestoreInstanceState(savedInstanceState);
		}
		bindData();
	}
	
	private void bindData() {
		ImageLoader.getInstance().displayImage(mUser.getAvatar(), mAvatar,
				options);
		
		mName.setText(mUser.getUsername());
		mAge.setText("" + mUser.getAge());
		mLocation.setText("" + mUser.getLocation());
		int resId;
		if(mUser.isMale()) {
			resId = R.drawable.male_little_icon;
		} else {
			resId = R.drawable.female_little_icon;
		}
		mSex.setImageResource(resId);
		
		final String sig = mUser.getSignature();
		if(TextUtils.isEmpty(sig)) {
			mSignature.setText("您还没添加签名信息");
		} else {
			mSignature.setText(sig);
		}
		if(mUser.hasPhotoInGallery()) {
			showGallery();
		}
	}
	
	private void showGallery() {
		if(mPhotoAdapter == null) {
			mPhotoAdapter = new ContactEditorPhotoListAdapter(mContext);
			mPhotos.setAdapter(mPhotoAdapter);
		}
		mPhotoAdapter.setData(mUser.getGallery());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * Recovery data when activity destroyed by system when situation is a shortage of resources 
	 * @param state
	 */
	private void onRestoreInstanceState(Bundle state) {
		
	}

	@Override
	public void onDetach() {
		mContext = null;
		super.onDetach();
	}

}
