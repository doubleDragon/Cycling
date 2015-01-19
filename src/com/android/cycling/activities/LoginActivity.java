package com.android.cycling.activities;

import cn.bmob.v3.listener.SaveListener;

import com.android.cycling.R;
import com.android.cycling.data.ServerUser;
import com.android.cycling.widget.HeaderLayout;
import com.android.cycling.widget.HeaderLayout.Action;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private EditText mEmail;
	private EditText mPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		initViews();
	}

	private void initViews() {
		mEmail = (EditText) findViewById(R.id.email);
		mPassword = (EditText) findViewById(R.id.pwd);
		
		HeaderLayout headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
		headerLayout.setTitle(R.string.login);
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
				readyToLogin();
			}
			
		});
	}
	
	private void readyToLogin() {
		final String email = mEmail.getEditableText().toString();
		final String pwd = mPassword.getEditableText().toString();

		if (TextUtils.isEmpty(email)) {
			toastToUser("邮件不能为空");
			return;
		}

		if (TextUtils.isEmpty(pwd)) {
			toastToUser("密码不能为空");
			return;
		}
		
		ServerUser user = new ServerUser();
		user.setUsername(email);
		user.setEmail(email);
		user.setPassword(pwd);
		user.login(this, new SaveListener() {
			@Override
			public void onSuccess() {
				toastToUser("登录成功:");
				finish();
			}

			@Override
			public void onFailure(int code, String msg) {
				toastToUser("登录失败:" + msg);
			}
		});
		
	}
	
	public void intentToRegiste(View v) {
		Intent i = new Intent(this, RegisteActivity.class);
		startActivity(i);
		finish();
	}
	
	private void toastToUser(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
}