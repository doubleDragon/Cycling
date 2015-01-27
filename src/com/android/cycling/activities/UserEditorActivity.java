package com.android.cycling.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobUser;

import com.android.cycling.CyclingActivity;
import com.android.cycling.R;
import com.android.cycling.data.ServerUser;
import com.android.cycling.secondhand.IssueEditorPhotoListAdapter;
import com.android.cycling.setting.SettingManager;
import com.android.cycling.util.DataUtils;
import com.android.cycling.util.ImageUtils;
import com.android.cycling.widget.HeaderLayout;
import com.android.cycling.widget.HeaderLayout.Action;
import com.android.cycling.widget.RoundedImageView;
import com.android.cycling.widget.SimpleGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UserEditorActivity extends CyclingActivity implements OnClickListener, SettingManager.Listener{
	
	public static final int REQUEST_SELECT_PICTURE = 1001;
	public static final int REQUEST_SELECT_AVATAR = 1002;
	private final String ADD_PHOTO_URI = "assets://addPhoto.png";
	
	private RoundedImageView mAvatar;
	private EditText mName;
	private EditText mSex;
	private EditText mAge;
	private EditText mSignature;
	private EditText mLocation;
	
	private SimpleGridView mGridView;
	private IssueEditorPhotoListAdapter mAdapter;
	
	private String mAvatarUriString;
	
	private ServerUser mSelfUser;
	
	private DisplayImageOptions options;
	
	private SettingManager mSettingManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor_activity);
		
		resovleIntent(getIntent());
		
		initImageLoader();
		
		initViews();
		initData();
		
		mSettingManager = new SettingManager(this);
		mSettingManager.setListener(this);
	}

	private void initImageLoader() {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	private void resovleIntent(Intent i) {
		mSelfUser = (ServerUser) i.getSerializableExtra("user");
		if(mSelfUser == null) {
			//toastToLogin
			mSelfUser = BmobUser.getCurrentUser(this, ServerUser.class);
		}
	}

	private void initData() {
		updateAvatar(mSelfUser.getAvatar());
		
		mName.setText(mSelfUser.getUsername());
		mSex.setText(mSelfUser.isMale() ? "男" : "女");
		mAge.setText("" + mSelfUser.getAge());
		mLocation.setText("" + mSelfUser.getLocation());
		mSignature.setText("" + mSelfUser.getSignature());
	}

	private void initViews() {
		HeaderLayout headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
		headerLayout.setTitle(R.string.information_editor);
		headerLayout.setHomeAction(new Action() {

			@Override
			public int getDrawable() {
				return R.drawable.back_indicator;
			}

			@Override
			public void performAction(View view) {
				finish();
			}
			
		});
		
		headerLayout.addAction(new Action() {

            @Override
            public int getDrawable() {
                return R.drawable.confirm_indicator;
            }

            @Override
            public void performAction(View view) {
            	readToSave();
            }

        });
		
		mAvatar = (RoundedImageView) findViewById(R.id.avatar);
		mAvatar.setOnClickListener(this);
		mName = (EditText) findViewById(R.id.name);
		mSex = (EditText) findViewById(R.id.sex);
		mAge = (EditText) findViewById(R.id.age);
		mSignature = (EditText) findViewById(R.id.signature);
		mLocation = (EditText) findViewById(R.id.location);
		
		
		mGridView = (SimpleGridView) findViewById(R.id.pictureList);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int count = mAdapter.getCount();
				if(count == 1 || position == count) {
					intentToSelectPicture();
				}
			}
			
		});
		mAdapter = new IssueEditorPhotoListAdapter(this);
		mGridView.setAdapter(mAdapter);
		setEmptyPicturesData();
	}
	
	/**
	 * insert one picture first,aim to add other pic
	 */
	private void setEmptyPicturesData() {
		List<String> data = new ArrayList<String>();
		data.add(ADD_PHOTO_URI);
		mAdapter.setData(data);
	}
	
	
	private void intentToSelectPicture() {
		Intent i =  new Intent(this, SelectPicturesActivity.class);
		startActivityForResult(i, REQUEST_SELECT_PICTURE);
	}
	
	private void setAdapterData(String[] uriPathArray) {
		if(uriPathArray == null || uriPathArray.length < 1) return;
		
		List<String> data = new ArrayList<String>(Arrays.asList(uriPathArray));
		if(data.size() < 6) {
			data.add(ADD_PHOTO_URI);
		}
		mAdapter.setData(data);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
		case REQUEST_SELECT_PICTURE:
			if(resultCode == Activity.RESULT_OK && data != null) {
				String[] uriPathArray = data.getStringArrayExtra(SelectPicturesActivity.EXTRA_PICTURE_URI);
				setAdapterData(uriPathArray);
			}
			break;
		case REQUEST_SELECT_AVATAR:
			if(data == null) return;
			Uri avatarUri = data.getData();
			if(avatarUri != null) {
				mAvatarUriString = ImageUtils.getPath(this, avatarUri);
				updateAvatar(DataUtils.FILE_URI_PREFIX + mAvatarUriString);
			}
			break;
		default:
			break;
		}
	}
	
	private void updateAvatar(String uri) {
		ImageLoader.getInstance().displayImage(uri, mAvatar,
				options);
	}
	
	@Override
	public void onComplete(boolean success) {
		stopDialog();
		if(success) {
			toastMessage("修改成功");
		} else {
			toastMessage("修改失败");
		}
		finishActivity();
	}
	
	/**
	 * First upload avatar, then upload gallery picture
	 * last update other info, such as age sex etc...
	 */
	private void readToSave() {
		startDialog(R.string.sending);
		
		final String name = mName.getEditableText().toString();
		final String age = mAge.getEditableText().toString();
		final String sex = mSex.getEditableText().toString();
		final String location = mLocation.getEditableText().toString();
		final String signature = mSignature.getEditableText().toString();
		
		final String[] galleryPhotoArray = mAdapter.getAllData();
		
		mSettingManager.updateUserInfo(mSelfUser, mAvatarUriString, name, age, 
				sex, location, signature, galleryPhotoArray);

	}
	
	private void finishActivity() {
		finish();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.avatar:
			selectAvatar();
			break;
		default:
			break;
		}
	}
	
	private void selectAvatar() {
		Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);  
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);  
        galleryIntent.setType("image/*");  
        startActivityForResult(galleryIntent, REQUEST_SELECT_AVATAR);
	}
	
	private void toastMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	
	private static final String TAG = UserEditorActivity.class.getSimpleName();
	private static final boolean DEBUG = true;
	private static void logW(String msg) {
		if(DEBUG) {
			Log.d(TAG, msg);
		}
	}

	
}