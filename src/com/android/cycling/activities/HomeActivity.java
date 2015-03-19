package com.android.cycling.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HomeActivity extends Activity {
	
	private MyHandler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.home_activity);
		
		mHandler = new MyHandler(this);
		mHandler.sendEmptyMessageDelayed(0, 2000);
	}
	
	private static class MyHandler extends Handler {
		
		private Activity context;
		
		public MyHandler(Activity context) {
			this.context = context;
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			intentToMainActivity();
		}
		
		private void intentToMainActivity() {
			Intent i = new Intent(context, MainActivity2.class);
			context.startActivity(i);
			context.finish();
		}
	}
}
