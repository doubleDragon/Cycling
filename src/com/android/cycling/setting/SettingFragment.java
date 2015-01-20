package com.android.cycling.setting;

import cn.bmob.v3.BmobUser;

import com.android.cycling.R;
import com.android.cycling.activities.LoginActivity;
import com.android.cycling.data.ServerUser;
import com.android.cycling.widget.HeaderLayout;
import com.android.cycling.widget.HeaderLayout.Action;
import com.android.cycling.widget.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingFragment extends Fragment implements OnClickListener{

	private static final String TAG = SettingFragment.class.getSimpleName();
	
	
	private Context mContext;
	private ServerUser mSelfUser;
	private boolean isNeedLogin;
	
	private RoundedImageView mAvatar;
	private Button mAlertBtn;
	
	private DisplayImageOptions options;

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
		
		//load self user data from server
		mSelfUser = BmobUser.getCurrentUser(mContext, ServerUser.class);
		if(mSelfUser == null) {
			isNeedLogin = true;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		mContext = null;
		super.onDetach();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.setting_fragment, container, false);
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
//				intentToEdite();
			}
			
		});
		
		View infoContainer = root.findViewById(R.id.infoContainer);
		mAvatar = (RoundedImageView) root.findViewById(R.id.avatar);
		mAvatar.setOnClickListener(this);
		
		mAlertBtn = (Button) root.findViewById(R.id.alertBtn);
		mAlertBtn.setOnClickListener(this);
		
		if(isNeedLogin) {
			mAlertBtn.setVisibility(View.VISIBLE);
			infoContainer.setVisibility(View.GONE);
		} else {
			mAlertBtn.setVisibility(View.GONE);
			infoContainer.setVisibility(View.VISIBLE);
			bindAvatar();
		}
	}
	
	private void bindAvatar() {
		String uriPath = mSelfUser.getAvatar();
		ImageLoader.getInstance().displayImage(uriPath, mAvatar,
				options);
	}
	
	private void intentToActivity(Class<? extends Activity> aClass) {
		Intent i =  new Intent(mContext, aClass);
		mContext.startActivity(i);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.avatar:
			//setting self avatar
			break;
		case R.id.alertBtn:
//			Intent i =  new Intent(mContext, LoginActivity.class);
//			mContext.startActivity(i);
			intentToActivity(LoginActivity.class);
			break;
		default:
			break;
		}
		
	}
}
