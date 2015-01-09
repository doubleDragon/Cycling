package com.android.cycling.activities;

import com.android.cycling.R;
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
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
		
		if(TextUtils.isEmpty(email) && TextUtils.isEmpty(nameAlias) &&
				TextUtils.isEmpty(pwd) && TextUtils.isEmpty(pwdConfirm)) {
			toastToUser("注册信息不能为空");
		}
		
		String[] params = new String[]{email, nameAlias, pwd};
		new RegisteTask().execute(params);
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
	
	private void toastToUser(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	
	private static void log(String msg) {
		android.util.Log.d("cyc", msg);
	}
	
	private class RegisteTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String result = NetworkUtils.registeToServer(mIsMale, 
					params[0], params[1], params[2]);
			log("registe result: " + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}
	
}
