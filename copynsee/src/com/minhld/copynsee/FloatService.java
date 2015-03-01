package com.minhld.copynsee;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class FloatService extends Service {
	private static int ID_NOTIFICATION = 2018;

	private WindowManager windowManager;
	private ImageView dictHead;

	boolean mHasDoubleClicked = false;
	long lastPressTime;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// add floating icon with along service
		addFloatingService();
	}
	
	private void addFloatingService(){
		// window manager
		windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);

		// floating icon
		dictHead = new ImageView(this);
		dictHead.setImageResource(R.drawable.ic_launcher);
		
		final WindowManager.LayoutParams params = 
				new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;

		windowManager.addView(dictHead, params);

		try {
			dictHead.setOnTouchListener(new View.OnTouchListener() {
				private WindowManager.LayoutParams paramsF = params;
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;

				@Override public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN: {
	
							// get current time in nanoseconds.
							long pressTime = System.currentTimeMillis();
	
	
							// If double click...
							if (pressTime - lastPressTime <= 300) {
								createNotification();
								FloatService.this.stopSelf();
								mHasDoubleClicked = true;
							}
							else {     // If not double click....
								mHasDoubleClicked = false;
							}
							lastPressTime = pressTime; 
							initialX = paramsF.x;
							initialY = paramsF.y;
							initialTouchX = event.getRawX();
							initialTouchY = event.getRawY();
							break;
						}
						case MotionEvent.ACTION_UP: {
							break;
						}
						case MotionEvent.ACTION_MOVE: {
							paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
							paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
							windowManager.updateViewLayout(dictHead, paramsF);
							break;
						}
					}
					return false;
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}

		dictHead.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//initiatePopupWindow(dictHead);
				//_enable = false;
			}
		});
	}
	
	private void createNotification(){
		Context context = getApplicationContext();
		Intent notificationIntent = new Intent(context, FloatService.class);
		PendingIntent pendingIntent = PendingIntent.getService(
								context, 0, notificationIntent, 0);
		
		String notifClickToStart = context.getString(R.string.notif_click_to_start);
		Notification notification = new Notification.Builder(context).
	    				setContentText(notifClickToStart).
	    				setSmallIcon(R.drawable.ic_launcher).
	    				setWhen(System.currentTimeMillis()).
	    				build();
		
		notification.flags = Notification.FLAG_AUTO_CANCEL | 
							Notification.FLAG_ONGOING_EVENT;

		NotificationManager notificationManager = (NotificationManager)context.
							getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(ID_NOTIFICATION,notification);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// remove the dictionary head
		if (dictHead != null) windowManager.removeView(dictHead);
	}
}
