package com.android.cycling.setting;

import com.android.cycling.R;
import com.android.cycling.activities.UserEditorActivity;
import com.android.cycling.data.ServerUser;
import com.android.cycling.secondhand.IssueListPhotoAdapter;
import com.android.cycling.widget.HeaderLayout;
import com.android.cycling.widget.RoundedImageView;
import com.android.cycling.widget.HeaderLayout.Action;
import com.android.cycling.widget.SimpleGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingContentFragment extends Fragment implements OnClickListener{
	
	private Context mContext;
	private ServerUser mSelfUser;
	
	private RoundedImageView mAvatar;
	private TextView mName;
	private TextView mAge;
	private TextView mLocation;
	private TextView mSignatue;
	private ImageView mSex;
	
	private SimpleGridView mPhotos;
	private SettingPhotoListAdapter mPhotoAdapter;
	
	private DisplayImageOptions options;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}
	
	public void setSelfUser(ServerUser user) {
		mSelfUser = user;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		android.util.Log.d("tag", "SettingContentFragment --- onCreate");
		super.onCreate(savedInstanceState);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public void onDetach() {
		mContext = null;
		super.onDetach();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		bindData();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.setting_content_fragment, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		View root = getView();
		if(root == null) {
			throw new IllegalStateException("Content view not yet created");
		}
		HeaderLayout headerLayout = (HeaderLayout) root.findViewById(R.id.header_layout);
		headerLayout.setTitle(R.string.indicator_title_index4);
		
		headerLayout.addAction(new Action() {

			@Override
			public int getDrawable() {
				return R.drawable.editor;
			}

			@Override
			public void performAction(View view) {
				intentToEdite();
			}
		});
		
		mAvatar = (RoundedImageView) root.findViewById(R.id.avatar);
		mName = (TextView) root.findViewById(R.id.name);
		mAge = (TextView) root.findViewById(R.id.age);
		mLocation = (TextView) root.findViewById(R.id.location);
		mSex = (ImageView) root.findViewById(R.id.sex);
		mSignatue = (TextView) root.findViewById(R.id.signatue_content);
		
		mPhotos = (SimpleGridView) root.findViewById(R.id.photos);
	}
	
	private void bindData() {
		updateAvatar();
		mName.setText(mSelfUser.getUsername());
		mAge.setText("" + mSelfUser.getAge());
		mLocation.setText("" + mSelfUser.getLocation());
		int resId;
		if(mSelfUser.isMale()) {
			resId = R.drawable.male_little_icon;
		} else {
			resId = R.drawable.female_little_icon;
		}
		mSex.setImageResource(resId);
		
		final String sig = mSelfUser.getSignature();
		if(TextUtils.isEmpty(sig)) {
			mSignatue.setText("您还没添加签名信息");
		} else {
			mSignatue.setText(sig);
		}
		if(mSelfUser.hasPhotoInGallery()) {
			showGallery();
		}
	}
	
	private void showGallery() {
		if(mPhotoAdapter == null) {
			mPhotoAdapter = new SettingPhotoListAdapter(mContext);
			mPhotos.setAdapter(mPhotoAdapter);
		}
		mPhotoAdapter.setData(mSelfUser.getGallery());
	}

	private void updateAvatar() {
		ImageLoader.getInstance().displayImage(mSelfUser.getAvatar(), mAvatar,
				options);
	}
	
	private void intentToEdite() {
		Intent i =  new Intent(mContext, UserEditorActivity.class);
		i.putExtra("user", mSelfUser);
		mContext.startActivity(i);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
