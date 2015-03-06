package com.minhld.copynsee;

import com.minhld.copynsee.business.UIProvider;
import com.minhld.copynsee.utils.Constant;
import com.minhld.copynsee.utils.Utils;

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
import android.widget.ImageView.ScaleType;

public class FloatService extends Service {

	private WindowManager windowManager;
	private ImageView dictHead;

	boolean mHasDoubleClicked = false;
	long lastPressTime;
	boolean longClickPress = false;

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
		// ------ window manager ------ 
		windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);

		// ------ setup floating icon ------ 
		dictHead = new ImageView(this);
		dictHead.setImageResource(R.drawable.ic_launcher);
		dictHead.setScaleType(ScaleType.FIT_XY);
		
		// set floating icon size
		int iconSize = (int)(Utils.getDisplayMatrics(this).scaledDensity * 
							Constant.SIZE_FLOATING_ICON);
		Utils.setFloatingIconSize(iconSize);
		
		final WindowManager.LayoutParams params = 
				new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 0;
		params.width = iconSize;
		params.height = iconSize + 1;
		
		// add the floating icon to the window list
		windowManager.addView(dictHead, params);

		try {
			// handle touch event - setup the double click and single
			// click in this part
			dictHead.setOnTouchListener(new View.OnTouchListener() {
				private WindowManager.LayoutParams paramsF = params;
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;

				@Override 
				public boolean onTouch(View v, MotionEvent event) {
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
			e.printStackTrace();
		}
		
		// ------ handle the long click ------ 
		dictHead.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Utils.toast(FloatService.this, "long click touch!");
				longClickPress = true;
				return false;
			}
		});
		
		// ------ handle single click ------ 
		dictHead.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!mHasDoubleClicked && !longClickPress){
					// if single click
					UIProvider.togglePopupWindow(FloatService.this, dictHead, false);
				}
				longClickPress = false;
			}
		});
	}
	
	ClipboardManager clipboardMngr;
	ClipboardManager.OnPrimaryClipChangedListener clipChangedListener = 
							new ClipboardManager.OnPrimaryClipChangedListener() {
		@Override
		public void onPrimaryClipChanged() {
			// open pop-up to display word meanings
			UIProvider.togglePopupWindow(FloatService.this, dictHead, true);
		}
	};
	
	/**
	 * this will listen to the system clip-board 
	 */
	private void listenClipboard(){
		clipboardMngr = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		
		// register with a listener, it will be remove when icon transforms
		// into the pending intent on the waiting bar
		clipboardMngr.addPrimaryClipChangedListener(clipChangedListener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// remove the clip-board listener
		clipboardMngr.removePrimaryClipChangedListener(clipChangedListener);
		
		// remove the floating icon
		if (dictHead != null) windowManager.removeView(dictHead);
	}
}
