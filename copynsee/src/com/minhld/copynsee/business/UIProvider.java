package com.minhld.copynsee.business;

import com.minhld.copynsee.FloatService;
import com.minhld.copynsee.R;
import com.minhld.copynsee.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

@SuppressLint("InflateParams")
public class UIProvider {
	private static int ID_NOTIFICATION = 2018;
	
	private static PopupWindow popupWindow = null;
	
	/**
	 * initialize the floating service with icon
	 * 
	 * @param context
	 */
	public static void initiateFloatingService(Context context){
		// initialize background service
		// to draw background floating icon & listen to clip-board
		context.startService(new Intent(context, FloatService.class));
	}
	
	/**
	 * toggle the floating pop-up window
	 * 
	 * @param context
	 * @param anchor
	 * @param forceOpenPopup
	 * 
	 */
	public static void togglePopupWindow(Context context, View anchor,
										boolean forceOpenPopup){
		if (popupWindow == null){
			initiatePopupWindow(context, anchor);
		}else{
			if (popupWindow.isShowing() && !forceOpenPopup){
				popupWindow.dismiss();
			}else{
				popupWindow.showAsDropDown(anchor, 0, -20);
			}
		}
	}

	/**
	 * initialize the pop-up window attaching to the floating icon
	 * 
	 * @param context
	 * @param anchor
	 */
	private static void initiatePopupWindow(Context context, View anchor) {
		try {
	        LayoutInflater popupInflater = (LayoutInflater)context.getSystemService(
	        								Context.LAYOUT_INFLATER_SERVICE);
	        int popupWidth = (int)(Utils.getDisplayMatrics().widthPixels / 1.8f);
	        int popupHeight = (int)(Utils.getDisplayMatrics().widthPixels / 1.5f);
	        View popupView = popupInflater.inflate(R.layout.popup_words, null);
	        
	        popupWindow = new PopupWindow(popupView);
	        popupWindow.setWidth(popupWidth);
	        popupWindow.setHeight(popupHeight);
	        popupWindow.showAsDropDown(anchor, 0, -20);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * this will place an icon to the tray of android system
	 * 
	 * @param context
	 */
	public static void createNotification(Context context){
		Intent notificationIntent = new Intent(context, FloatService.class);
		PendingIntent pendingIntent = PendingIntent.getService(
								context, 0, notificationIntent, 0);

		String notifClickToStart = context.getString(
						R.string.notif_click_to_start);
		Notification notification = new Notification.Builder(context).
	    				setContentText(notifClickToStart).
	    				setSmallIcon(R.drawable.ic_launcher).
	    				setWhen(System.currentTimeMillis()).
	    				setContentIntent(pendingIntent).
	    				build();
		notification.flags = Notification.FLAG_AUTO_CANCEL | 
							Notification.FLAG_ONGOING_EVENT;

		NotificationManager notificationManager = (NotificationManager)
						context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(ID_NOTIFICATION,notification);
	}

}
