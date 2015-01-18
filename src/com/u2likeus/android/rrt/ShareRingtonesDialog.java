package com.u2likeus.android.rrt;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

public class ShareRingtonesDialog extends Dialog {
		
	Context mContext;
	LinearLayout llShare_menu;
	
	public ShareRingtonesDialog(Context context, String mixes_id){
		super(context);
		mContext = context;
		setContentView(R.layout.share_ringtone_dialog);
		
		final String mix_id = mixes_id;
		
		setTitle("Share this Ringtone");
		
		Button btnTwitter = (Button)findViewById(R.id.btnTwitter);
		btnTwitter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				dismiss();
												
				WebViewDialog wvDlog = new WebViewDialog(mContext);				
				wvDlog.getURL("http://random-ringtones.com/mashup/tw/post?mix_id="+mix_id);
				wvDlog.show();
			}
		});
		
		Button btnFacebook = (Button)findViewById(R.id.btnFacebook);
		btnFacebook.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				
				dismiss();
				
				WebViewDialog wvDlog = new WebViewDialog(mContext);
				wvDlog.getURL("http://random-ringtones.com/mashup/fb/post?mix_id="+mix_id);
				wvDlog.show();
			}
		});
		
		Button btnKakao = (Button)findViewById(R.id.btnKakao);
		btnKakao.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
								
			}
		});
	}		
	
}
