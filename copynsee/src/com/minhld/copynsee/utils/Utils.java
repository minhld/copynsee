package com.minhld.copynsee.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class Utils {
	private static DisplayMetrics dispMetrics;
	
	public static void initUtils(Context context){
		dispMetrics = new DisplayMetrics();
		Display display = ((WindowManager) context.getSystemService(
					Context.WINDOW_SERVICE)).getDefaultDisplay();
		display.getRealMetrics(dispMetrics);
	}
	
	public static DisplayMetrics getDisplayMatrics(){
		return Utils.dispMetrics;
	}
	
	public static void toast(Context context, int msgId){
		Toast.makeText(context, msgId, 2000).show();
	}

	public static void toast(Context context, int msgId, int duration){
		Toast.makeText(context, msgId, duration).show();
	}
	
	public static void toast(Context context, String msgStr){
		Toast.makeText(context, msgStr, 2000).show();
	}

	public static void toast(Context context, String msgStr, int duration){
		Toast.makeText(context, msgStr, duration).show();
	}

}
