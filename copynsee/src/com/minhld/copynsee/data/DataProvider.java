package com.minhld.copynsee.data;

import com.minhld.copynsee.R;
import com.minhld.copynsee.utils.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;

public class DataProvider {
	static SQLiteDatabase dbConnector = null;
	static DbListener listener;
	
	public static void setDbListener(DbListener listener){
		DataProvider.listener = listener;
	}
	
	/**
	 * open the DB connection in synchronization mode
	 * 
	 * @param context
	 */
	public static void openDbSync(final Context context){
		new AsyncTask<Void,Integer,Void>(){
			boolean loadDbValue = false;
			@Override
			protected Void doInBackground(Void... params) {
				loadDbValue = DataProvider.openDb(context);
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Integer... values) {
				if (DataProvider.listener != null){
					DataProvider.listener.dbLoaded(values[0]);
				}
			}
			
		}.execute();
		
	}
	
	/**
	 * this will open a database 
	 * the connector will then be hold for later usage
	 * 
	 * @param context
	 * @return
	 */
	public static boolean openDb(Context context){
		String evFileName = context.getString(R.string.data_av_file);
		String evPath = Utils.getDbPath(context) + "/" + evFileName;
		
		try{
			dbConnector = SQLiteDatabase.openDatabase(
						evPath, null, SQLiteDatabase.OPEN_READWRITE);
			return true;
		}catch(SQLiteException e){
			return false;
		}
	}
	
	/**
	 * this will close the database 
	 * 
	 * @return
	 */
	public static boolean closeDb(){
		if (dbConnector != null){
			dbConnector.close();
			return true;
		}
		return false;
	}
	
	public interface DbListener{
		public void dbLoaded(int status, Object... data);
	}
}
