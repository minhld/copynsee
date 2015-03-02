package com.minhld.copynsee;

import com.minhld.copynsee.business.UIProvider;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class FloatService extends Service {

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
		
		// add listener to the system clip-board
		listenClipboard();
	}
	
	/**
	 * this will add floating icon and its related click events
	 * to the android desktop
	 */
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
	
	
							// if double click
							if (pressTime - lastPressTime <= 300) {
								UIProvider.createNotification(FloatService.this);
								FloatService.this.stopSelf();
								mHasDoubleClicked = true;
							}
							else {     
								// if not double click
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
	
	/**
	 * this will listen to the system clip-board 
	 */
	private void listenClipboard(){
		ClipboardManager clipboardMngr = (ClipboardManager)
							getSystemService(CLIPBOARD_SERVICE);
		clipboardMngr.addPrimaryClipChangedListener(new ClipboardManager.
									OnPrimaryClipChangedListener() {
			@Override
			public void onPrimaryClipChanged() {
				// open pop-up to display word meanings
				UIProvider.initiatePopupWindow(FloatService.this, dictHead);
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// remove the dictionary head
		if (dictHead != null) windowManager.removeView(dictHead);
	}
}
