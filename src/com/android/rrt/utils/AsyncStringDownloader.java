package com.android.rrt.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncStringDownloader extends Object {
	public interface OnCompletedListener {
		public void onCompleted(String url, String result);
		public void onErrorRaised(String url, Exception e);
	}
	
	public class AsyncStringDownloadTask extends AsyncTask<String, Integer, String> {
		String url;
		OnCompletedListener mListener;
		
		public AsyncStringDownloadTask(String url, OnCompletedListener onCompletedListener) {
			this.url = url;
			this.mListener = onCompletedListener;
		}
		
		@Override
		protected String doInBackground(String... params) {
			try {
				HttpURLConnection request = getRequest();
				request.connect();
				BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
				StringBuilder result = new StringBuilder();
				while(true) {
					String line = br.readLine();
					if( line == null ) break;
					result.append(line + "\n");
				}
				
				return result.toString();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		protected void onPostExecute(String result) {
			if( result == null ) {
				if( mListener != null ) {
					mListener.onErrorRaised(url, null);
				}
			} else if( result.length() == 0 ) {
				Log.i("oddm", "retry download (" + url + ")");
				addTask(url, mListener);
				return;
			}
			
			if( mListener != null ) {
				mListener.onCompleted(url, result);
			}
		};
		
		protected HttpURLConnection getRequest() throws IOException {
			return (HttpURLConnection) new URL(url).openConnection();
		}
	}
	
	protected static AsyncStringDownloader mAsyncStringDownloader;
	protected AsyncStringDownloader() {}
	
	public static AsyncStringDownloader getInstance() {
		if( mAsyncStringDownloader == null ) {
			mAsyncStringDownloader = new AsyncStringDownloader();
		}
		
		return mAsyncStringDownloader;
	}
	
	protected void addTask(String url, OnCompletedListener listener) {
		AsyncStringDownloadTask task = new AsyncStringDownloadTask(url, listener);
		task.execute(url);
	}
	
	public static void download(String url, OnCompletedListener listener) {
		AsyncStringDownloader.getInstance().addTask(url, listener);
	}
}
