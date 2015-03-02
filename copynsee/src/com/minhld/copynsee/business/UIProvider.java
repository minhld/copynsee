package com.minhld.copynsee.business;

import com.minhld.copynsee.R;
import com.minhld.copynsee.utils.Utils;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

@SuppressLint("InflateParams")
public class UIProvider {
	private static int ID_NOTIFICATION = 2018;

	public static void initiatePopupWindow(Context context, View anchor) {
		try {
	        LayoutInflater popupInflater = (LayoutInflater)context.getSystemService(
	        								Context.LAYOUT_INFLATER_SERVICE);
	        int popupWidth = (int)(Utils.getDisplayMatrics().widthPixels / 1.8f);
	        int popupHeight = (int)(Utils.getDisplayMatrics().widthPixels / 1.5f);
	        View popupView = popupInflater.inflate(R.layout.popup_words, null);
	        PopupWindow popupWindow = new PopupWindow(popupView);
	        popupWindow.setWidth(popupWidth);
	        popupWindow.setHeight(popupHeight);
	        popupWindow.showAsDropDown(anchor, 100, 100);                
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * this will place an icon to the tray of android system
	 */
	public static void createNotification(Context context){

		String notifClickToStart = context.getString(
						R.string.notif_click_to_start);
		Notification notification = new Notification.Builder(context).
	    				setContentText(notifClickToStart).
	    				setSmallIcon(R.drawable.ic_launcher).
	    				setWhen(System.currentTimeMillis()).
	    				build();
		
		notification.flags = Notification.FLAG_AUTO_CANCEL | 
							Notification.FLAG_ONGOING_EVENT;

		NotificationManager notificationManager = (NotificationManager)
						context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(ID_NOTIFICATION,notification);
	}

}
