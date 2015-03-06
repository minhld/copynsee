package com.minhld.copynsee.business;

import butterknife.ButterKnife;

import com.minhld.copynsee.FloatService;
import com.minhld.copynsee.R;
import com.minhld.copynsee.utils.Constant;
import com.minhld.copynsee.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

/**
 * This class will provide basic and commonly use function
 * for the UI.
 * 
 * @author minhld
 *
 */
@SuppressLint("InflateParams")
public class UIProvider {
	private static int ID_NOTIFICATION = 2018;

	private static EditText searchText;
	private static ImageView searchBtn;
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
	
	public static void setClipboard(ClipData clipData){
		String clipboardText = Constant.EMPTY;
		for (int i = 0; i < clipData.getItemCount(); i++){
			clipboardText += clipData.getItemAt(i).getText();
		}
		searchText.setText(clipboardText);
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
			}else if (!popupWindow.isShowing()){
				popupWindow.showAsDropDown(anchor, 0, 0);
//				popupWindow.setFocusable(true);
//				popupWindow.update();
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
	        View popupView = popupInflater.inflate(R.layout.popup_words, null);
	        
	        int popupWidth = Utils.getFloatingIconSize() * 5;
	        int popupHeight = (int)(popupWidth * 1.2f);
	        popupWindow = new PopupWindow(popupView);
	        popupWindow.setWidth(popupWidth);
	        popupWindow.setHeight(popupHeight);
	        
	        popupWindow.showAsDropDown(anchor, 0, 0);
	        
	        searchText = (EditText)popupView.findViewById(R.id.searchText);
	        searchBtn = (ImageView)popupView.findViewById(R.id.searchBtn);
	        
//	        popupWindow.setFocusable(true);
//	        popupWindow.update();
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
		// create the pending intent, so that it will wake up the 
		// floating icon when user click on its item on waiting bar
		Intent notificationIntent = new Intent(context, FloatService.class);
		PendingIntent pendingIntent = PendingIntent.getService(
								context, 0, notificationIntent, 0);

		String notifClickToStart = context.getString(R.string.notif_click_to_start);
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
		
		// close floating window if it is available
		if (popupWindow != null && popupWindow.isShowing()){
			popupWindow.dismiss();
		}
	}

}
