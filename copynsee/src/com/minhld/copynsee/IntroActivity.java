package com.minhld.copynsee;

import com.minhld.copynsee.R;
import com.minhld.copynsee.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class IntroActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		new AsyncTask<Void,Integer,Void>(){
			
			@Override
			protected Void doInBackground(Void... params){
				try{
					Utils.initUtils(IntroActivity.this);
					Thread.sleep(2000);
				}catch(Exception e){ } 
				publishProgress(0);
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Integer... values){
				// initialize background service
				// to draw background floating icon & listen to clip-board
				startService(new Intent(IntroActivity.this, FloatService.class));
				
				// remove the current introduction window
				finish();
				Utils.toast(IntroActivity.this, R.string.ui_notice_run_in_bg, 3000);
			}
		}.execute();
	}

}
