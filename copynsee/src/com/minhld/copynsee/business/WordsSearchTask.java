package com.minhld.copynsee.business;

import com.minhld.copynsee.utils.Constant;

import android.os.AsyncTask;

public class WordsSearchTask extends AsyncTask<Void, Integer, Void>{
	WordsSearchListener searchListener;
	String words = Constant.EMPTY;
	
	public WordsSearchTask(String words, WordsSearchListener searchListener){
		this.words = words;
		this.searchListener = searchListener;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		
	}

	public interface WordsSearchListener {
		public void wordsSearchDone(boolean searchResult, String msg);
	}
}
