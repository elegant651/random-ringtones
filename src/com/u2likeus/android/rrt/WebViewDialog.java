package com.u2likeus.android.rrt;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewDialog extends Dialog {

	WebView mWebView;
	
	public WebViewDialog(Context context){
		super(context);
		setContentView(R.layout.webviewdialog);
		
		setTitle("Share Ringtones");		
	}
	
	public void getURL(String path){					
				
		mWebView = (WebView)findViewById(R.id.webview);		
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setUserAgentString("AndroidWebView");
		mWebView.clearCache(true);
		mWebView.loadUrl(path);		
		mWebView.setWebViewClient(new MyWebViewClient());
		
	}
	
	  
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) { 
            mWebView.goBack(); 
            return true; 
        } 
        return super.onKeyDown(keyCode, event);

    }
	
	private class MyWebViewClient extends WebViewClient {
		
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
			super.onReceivedError(view, errorCode, description, failingUrl);
			
			System.out.println("error: " +errorCode);
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			
			System.out.println("url: " +url);
			
			if(url.contains("post_result=ok")){				
				
				dismiss();
				return false;
			}
			view.loadUrl(url);
			return true;
		}
	}	
}
