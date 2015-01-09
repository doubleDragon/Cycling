package com.android.cycling.activities;

import cn.bmob.v3.listener.SaveListener;

import com.android.cycling.R;
import com.android.cycling.data.CyclingUser;
import com.android.cycling.util.NetworkUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

/**
 * 
 * Url:http://api.lovebiking.cn/Home/Registration
 * Success when return ok from server
 * @author wsl
 */

public class RegisteActivity extends Activity implements View.OnClickListener{
	
	private static final String TAG = RegisteActivity.class.getSimpleName();
	private static final boolean DEBUG = false;
	
	private Button mConfirmRegiste;
	private Button mBack;
	private EditText mEmail;
	private EditText mNameAlias;
	private EditText mLoginPassward;
	private EditText mLoginPasswardConfirm;
	private ImageView mMaleIndicate;
	private ImageView mFemaleIndicate;
	
	private boolean mIsMale;

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
	
	private void initViews() {
		mConfirmRegiste = (Button) findViewById(R.id.confirm);
		mBack = (Button) findViewById(R.id.back);
		mConfirmRegiste.setOnClickListener(this);
		mBack.setOnClickListener(this);
		
		mMaleIndicate = (ImageView) findViewById(R.id.male);
		mFemaleIndicate = (ImageView) findViewById(R.id.female);
		mMaleIndicate.setOnClickListener(this);
		mFemaleIndicate.setOnClickListener(this);
		
		mEmail = (EditText) findViewById(R.id.email);
		mNameAlias = (EditText) findViewById(R.id.nickname);
		mLoginPassward = (EditText) findViewById(R.id.pwd);
		mLoginPasswardConfirm = (EditText) findViewById(R.id.pwdConfirm);
		
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
		
		CyclingUser user = new CyclingUser();
		user.setUsername(email);
		user.setPassword(pwd);
		user.setEmail(email);
		user.setEmailVerified(true);
		user.setMale(mIsMale);
		user.setAvatar("");
		user.signUp(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				logW("registe success");
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				logW("registe failed");
			}
		});
		
//		String[] params = new String[]{email, nameAlias, pwd};
//		new RegisteTask().execute(params);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.confirm:
			readyToRegiste();
			break;
		case R.id.back:
			finish();
			break;
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
