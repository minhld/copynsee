package com.minhld.copynsee;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.WindowManager;
import android.widget.ImageView;

public class FloatService extends Service {
	
	private WindowManager windowManager;
	private ImageView dictHead;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);

		// floating icon
		dictHead = new ImageView(this);
		dictHead.setImageResource(R.drawable.ic_launcher);
		
	}
}
