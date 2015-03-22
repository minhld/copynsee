package com.minhld.copynsee;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.minhld.copynsee.R;
import com.minhld.copynsee.business.DataDownloader;
import com.minhld.copynsee.business.UIProvider;
import com.minhld.copynsee.components.CircularImageView;
import com.minhld.copynsee.components.CircularProgressBar;
import com.minhld.copynsee.utils.Utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class IntroActivity extends Activity {

	@InjectView(R.id.introImg) CircularImageView introImg;
	@InjectView(R.id.loadProg) CircularProgressBar loadProg;
	@InjectView(R.id.statusTxt) TextView statusTxt;
	
	boolean isAppExist = false;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		ButterKnife.inject(this);
		
		new AsyncTask<Void,Integer,Void>(){
			
			@Override
			protected Void doInBackground(Void... params){
				// get configuration and application availability
				Utils.initUtils(IntroActivity.this);

				// prepare for data downloading. 
				DataDownloader downloader = new DataDownloader(
										IntroActivity.this);
				downloader.setBookDownloadListener(new DataDownloader.BookDownloadListener() {
					@Override
					public void update(int code, int progress) {
						switch (code){
							case DataDownloader.STATUS_STARTING : {
								loadProg.setVisibility(View.VISIBLE);
								break;
							}
							case DataDownloader.STATUS_ONGOING : {
								loadProg.setProgress(progress);
								loadProg.setTitle(progress + "%");
								loadProg.setSubTitle(getString(R.string.text_download_data));
								break;
							}
							case DataDownloader.STATUS_UNZIP_FILE : {
								loadProg.setSubTitle(getString(R.string.text_download_unzip));
								break;
							}
							case DataDownloader.STATUS_FINISHED : {
								loadProg.setVisibility(View.GONE);
								publishProgress(0);
								break;
							}
							case DataDownloader.STATUS_FILE_EXISTED : {
								loadProg.setVisibility(View.GONE);
								Utils.sleep(2000);
								publishProgress(0);
								break;
							}
							default : {
								loadProg.setVisibility(View.GONE);
								statusTxt.setVisibility(View.VISIBLE);
								Utils.vibrate(IntroActivity.this, 500);
								//Utils.sleep(3000);
								//System.exit(0);
								break;
							}
						}
					}
				});
				downloader.execute();
				
				//
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Integer... values){
				// start floating service
				UIProvider.initiateFloatingService(IntroActivity.this);
				
				// remove the current introduction window
				finish();
				Utils.toast(IntroActivity.this, R.string.ui_notice_run_in_bg, 3000);
			}
		}.execute();
	}

}
