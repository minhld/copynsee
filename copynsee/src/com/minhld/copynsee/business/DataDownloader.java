package com.minhld.copynsee.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.minhld.copynsee.R;
import com.minhld.copynsee.utils.Constant;
import com.minhld.copynsee.utils.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * this will help downloading data on different thread
 * 
 * @author minhld
 *
 */
public class DataDownloader extends AsyncTask<Void,Integer,Void>{
	
	final int STATUS_FILE_EXISTED = -1;
	final int STATUS_CONNECTION_LOST = -2;
	final int STATUS_DOWNLOAD_FAILED = -3;
	final int STATUS_FINISHED = 0;
	final int STATUS_UNZIP_FILE = 1;
	
	private Context context;
	private ProgressDialog progressor=null;
	private BookDownloadListener downloadListener;
	
	public void setBookDownloadListener(
				BookDownloadListener downloadListener){
		this.downloadListener=downloadListener;
	}
	
	public DataDownloader(Context ctx){
		this.context=ctx;
		this.progressor=new ProgressDialog(ctx);
	}
	
	@Override
	protected void onPreExecute(){
		progressor.setMessage(context.getResources().getString(
						R.string.text_download_data));
		progressor.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressor.setCancelable(false);
		progressor.setProgress(0);
		progressor.setMax(100);
		progressor.show();
	}
	
	@Override
	protected Void doInBackground(Void... params){
		InputStream input = null;
		OutputStream output = null;
		String dbBasePath = Utils.getDbPath(context);
		String dataUrl = context.getString(R.string.data_url);
		
		String dataFileName = context.getString(R.string.data_file);
		String zipDlPath = dbBasePath + "/" + dataFileName;
		
		String dataAVFileName = context.getString(R.string.data_av_file);
		String dbAVPath = dbBasePath + "/" + dataAVFileName;
		if (new File(dbAVPath).exists()){
			// if the files have already been downloaded, they will
			// not be downloaded again
			publishProgress(STATUS_FILE_EXISTED);
			return null;
		}
		
		try{
			URL url = new URL(dataUrl);
			HttpsURLConnection connection = (HttpsURLConnection)
										url.openConnection();
	        connection.setDoInput(true);
	        connection.setConnectTimeout(Constant.SYNC_TIMEOUT);
	        connection.connect();
	        
	        int retCode = connection.getResponseCode();
	        if (retCode == Constant.SYNC_CODE_OK){
	        	input = connection.getInputStream();
	        }else{
	        	publishProgress(STATUS_CONNECTION_LOST);
	        	return null;
	        }
	        
	        output = new FileOutputStream(zipDlPath, false);
	        
	        int totalBytesRead = 0;
	        int estimateBytes = connection.getContentLength();
	        byte[] buff = new byte[8192];
	        int bytesRead = 0;
	        while((bytesRead = input.read(buff)) != -1){
	        	output.write(buff, 0, bytesRead);
	        	totalBytesRead += bytesRead;
	        	// update progress 
	        	publishProgress((int)(((double)totalBytesRead /
	        				(double)estimateBytes) * (double)100));
	        }
	        output.flush();
	        output.close();
	        
	        // extract the ZIP file
	        publishProgress(STATUS_UNZIP_FILE);
	        Utils.unzipFile(zipDlPath, dbBasePath);
	        // and delete the saved EPUB file
	        Utils.delete(new File(zipDlPath));
	        
	        
	        publishProgress(STATUS_FINISHED);
			return null;
		}catch(Exception e){
			publishProgress(STATUS_DOWNLOAD_FAILED);
		}finally{
			try {
				if (input != null){
					input.close();
				}
			}catch(IOException e){ }
		}
		
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		if (values[0] == STATUS_FILE_EXISTED){
			progressor.dismiss();
		}else if (values[0] == STATUS_CONNECTION_LOST){
			progressor.dismiss();
			Utils.toast(context, R.string.text_download_connection_lost, 3000);
		}else if (values[0] >= 0 && values[0] <= 100){
			progressor.setProgress(values[0]);
			return;
		}else if (values[0] == STATUS_UNZIP_FILE){
			progressor.setMessage(this.context.getResources().getString(
									R.string.text_download_unzip));
			return;
		}else if (values[0] == STATUS_FINISHED){
			progressor.dismiss();
			Utils.toast(context, R.string.text_download_finished);
			
			if (downloadListener!=null){
				downloadListener.downloadDone(STATUS_FINISHED);
			}
		}else{
			// when value is incorrect
			progressor.dismiss();
			return;
		}
		
	}
	
	public interface BookDownloadListener{
		public void downloadDone(int resultCode);
	}
	
}
