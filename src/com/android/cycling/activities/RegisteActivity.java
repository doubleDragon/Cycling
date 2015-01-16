package com.android.cycling.activities;

import cn.bmob.v3.listener.SaveListener;

import com.android.cycling.R;
import com.android.cycling.data.ServerUser;
import com.android.cycling.widget.HeaderLayout;
import com.android.cycling.widget.HeaderLayout.Action;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.text.TextUtils;
import android.view.View;

/**
 * 
 * Url:http://api.lovebiking.cn/Home/Registration
 * Success when return ok from server
 * @author wsl
 */

public class RegisteActivity extends Activity implements View.OnClickListener{
	
	private static final String TAG = RegisteActivity.class.getSimpleName();
	private static final boolean DEBUG = false;
	
	private EditText mEmail;
	private EditText mNameAlias;
	private EditText mLoginPassward;
	private EditText mLoginPasswardConfirm;
	private ImageView mMaleIndicate;
	private ImageView mFemaleIndicate;
	
	private boolean mIsMale;
	
	private ProgressDialog mDisplayDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registe_activity);

		initData();
		initViews();
	}
	
	private void initData() {
		mIsMale = true;
	}
	
	private void startDialog() {
		if(mDisplayDialog != null) {
			mDisplayDialog.cancel();
			mDisplayDialog = null;
		}
		mDisplayDialog = new ProgressDialog(this);
		mDisplayDialog.setCancelable(false);
		mDisplayDialog.setCanceledOnTouchOutside(false);
		mDisplayDialog.setMessage(getResources().getString(R.string.sending));
		mDisplayDialog.show();
	}
	
	private void stopDialog() {
		if (mDisplayDialog != null) {
			mDisplayDialog.dismiss();
		}
	}
	
	private void initViews() {
		
		mMaleIndicate = (ImageView) findViewById(R.id.male);
		mFemaleIndicate = (ImageView) findViewById(R.id.female);
		mMaleIndicate.setOnClickListener(this);
		mFemaleIndicate.setOnClickListener(this);
		
		mEmail = (EditText) findViewById(R.id.email);
		mNameAlias = (EditText) findViewById(R.id.nickname);
		mLoginPassward = (EditText) findViewById(R.id.pwd);
		mLoginPasswardConfirm = (EditText) findViewById(R.id.pwdConfirm);
		
		HeaderLayout headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
		headerLayout.setTitle(R.string.registe);
		
		headerLayout.setHomeAction(new Action() {
			
			@Override
			public void performAction(View view) {
				finish();
			}
			
			@Override
			public int getDrawable() {
				// TODO Auto-generated method stub
				return R.drawable.back_indicator;
			}
		});
		headerLayout.addAction(new Action() {

			@Override
			public int getDrawable() {
				return R.drawable.confirm_indicator;
			}

			@Override
			public void performAction(View view) {
				readyToRegiste();
			}
			
		});
		
	}
	
	private void readyToRegiste() {
		String email = mEmail.getEditableText().toString();
		String nameAlias = mNameAlias.getEditableText().toString();
		String pwd = mLoginPassward.getEditableText().toString();
		String pwdConfirm = mLoginPasswardConfirm.getEditableText().toString();
		
		if(TextUtils.isEmpty(email) || TextUtils.isEmpty(nameAlias) ||
				TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwdConfirm)) {
			toastToUser(R.string.empty_registe_info);
		}
		
		if(!pwd.equals(pwdConfirm)) {
			toastToUser(R.string.confirm_pwd_wrong);
		}

		startDialog();
		
		ServerUser user = new ServerUser();
		user.setUsername(email);
		user.setPassword(pwd);
		user.setEmail(email);
		user.setEmailVerified(true);
		user.setMale(mIsMale);
		user.setAvatar("");
		user.signUp(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				stopDialog();
				logW("registe success");
				toastToUser("注册成功");
				finish();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				stopDialog();
				logW("registe failed");
				toastToUser("注册失败:" + arg1);
				
			}
		});
		
//		String[] params = new String[]{email, nameAlias, pwd};
//		new RegisteTask().execute(params);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.male:
			mIsMale = true;
			updateSexIndicator();
			break;
		case R.id.female:
			mIsMale = false;
			updateSexIndicator();
			break;
		}
	}
	
	private void updateSexIndicator() {
		if(mIsMale) {
			mMaleIndicate.setImageResource(R.drawable.male_sex_2);
			mFemaleIndicate.setImageResource(R.drawable.female_sex_1);
		} else {
			mMaleIndicate.setImageResource(R.drawable.male_sex_1);
			mFemaleIndicate.setImageResource(R.drawable.female_sex_2);
		}
	}
	
	private void toastToUser(int textResId) {
		Toast.makeText(this, textResId, Toast.LENGTH_SHORT).show();
	}
	
	private void toastToUser(String textResId) {
		Toast.makeText(this, textResId, Toast.LENGTH_SHORT).show();
	}
	
	
	private static void logW(String msg) {
		if(DEBUG) {
			android.util.Log.d(TAG, msg);
		}
	}
	
//	private class RegisteTask extends AsyncTask<String, String, String> {
//
//		@Override
//		protected String doInBackground(String... params) {
//			String result = NetworkUtils.registeToServer(mIsMale, 
//					params[0], params[1], params[2]);
//			logW("registe result: " + result);
//			return result;
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			super.onPostExecute(result);
//		}
//	}
	
}
