package com.u2likeus.android.rrt;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import net.daum.adam.publisher.AdView;
import net.daum.adam.publisher.AdView.AnimationType;
import net.daum.adam.publisher.AdView.OnAdClickedListener;
import net.daum.adam.publisher.AdView.OnAdFailedListener;
import net.daum.adam.publisher.AdView.OnAdLoadedListener;
import net.daum.adam.publisher.AdView.OnAdWillLoadListener;
import net.daum.adam.publisher.impl.AdError;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.android.rrt.data.RRTData;
import com.android.rrt.utils.AsyncStringDownloader;
import com.android.rrt.utils.RRTUserToken;
import com.kakao.talk.KakaoLink;


public class RandomRingtonesActivity extends Activity implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener {
		
	public static final int COPYRIGHT_MENU_ID = Menu.FIRST;
	public static final String MIXES_PATH = "http://random-ringtones.com/apis/mixes/random.json";
	public static final String LOGS_PATH = "http://random-ringtones.com/apis/logs/";
	public static final String RANKINGS_PATH = "http://random-ringtones.com/apis/ranks.json";
	public static final String MYBOX_SAVE_PATH = "http://random-ringtones.com/apis/mixes/my/save.json";
	public static final String MYBOX_LIKE_PATH = "http://random-ringtones.com/apis/mixes/my/like.json";
	
	public Context mContext;
	private RandomRingtonesActivity mActivity;
	TabHost thMain;
	String mUserToken = "";
	
	private AdView adView = null;
	private AdView adView2 = null;
	private AdView adView3 = null;
	
	//1st tab _ play
	Button mBtnLoad;
	RelativeLayout llLoad;
	U2Wheeling wheel;
	LoadPlayButton loadplaybtn;
	LinearLayout llTop_tab1;
	MediaPlayer mp;
	Animation loadAnim;
	Animation playAnim;
	ImageView likeButton;
	ImageView nextButton;
	Thread mThread;		
	
	ImageView imgCircleOn;
	private Boolean isLoading = false;
	public Boolean isPrepare = false;
	Boolean isPlay = false;
	Boolean isFirstPlay = false;
	long thistime;
	
	String mixes_id;
	String mixes_path = "";
	String mixes_play_count = "";
	String mixes_save_count = "";
	String mixes_like_count = "";
	String mixes_share_count = "";
	String mixes_reptitle = "";
	String[] mixes_titles_cnt;
	String[] mixes_titles_title;
	
	ProgressThread progressThread;
	ProgressDialog progressDialog;
	MakeRingtonesDialog mrDialog;
	
	
	//2nd, tab _ ranking  //3rd, tab _ mybox	
	public ArrayList<RRTData> alRanking = new ArrayList<RRTData>();
	public ArrayList<RRTData> alMybox = new ArrayList<RRTData>();
	Button btnRankingSave;
	Button btnRankingLike;
	Button btnMySaveRingtones;
	Button btnMyLikeRingtones;	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        mContext = this;
        mActivity = this;
        
        setContentView(R.layout.main);
        
        mUserToken = RRTUserToken.getInstance(this).getUserToken();

        getJsonObj(MIXES_PATH);        
        
        bindViews();        
        createTabs();          
    }        
    
    private void bindViews(){
    	thMain = (TabHost)findViewById(R.id.thMain);     	    	
    }
    
    private void createTabs(){
    	
    	thMain.setup();
    	
    	TabHost.TabSpec spec2 = thMain.newTabSpec("RANKING");
    	spec2.setIndicator("", getResources().getDrawable(R.drawable.btn_ranking_on));
    	spec2.setContent(R.id.llRanking);
    	thMain.addTab(spec2);
    	tab2_ranking();
    	
    	TabHost.TabSpec spec1 = thMain.newTabSpec("PLAY");
    	spec1.setIndicator("", getResources().getDrawable(R.drawable.btn_play_on));
    	spec1.setContent(R.id.llPlay);    	
    	thMain.addTab(spec1);
    	tab1_play();
  	
    	TabHost.TabSpec spec3 = thMain.newTabSpec("MYBOX");
    	spec3.setIndicator("", getResources().getDrawable(R.drawable.btn_favorite_on));    	
    	spec3.setContent(R.id.llMybox);    	
    	thMain.addTab(spec3);
    	tab3_mybox();
    	
    	thMain.setCurrentTab(1);
    	
    	thMain.setOnTabChangedListener(new OnTabChangeListener(){
    		public void onTabChanged(String tabId){
    			if(tabId == "RANKING"){   	    				
    				getJsonObj(RANKINGS_PATH+"?type=save&range=all");
    			}else if(tabId == "PLAY"){
    				//thMain.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.btn_ranking_on);
    			}else{
    				getJsonObj(MYBOX_SAVE_PATH+"?user_token="+mUserToken);
    			}
    		}
    	});    	
    }
    
    private void initAdam(){
    	adView = (AdView)findViewById(R.id.adview);
    	adView2 = (AdView)findViewById(R.id.adview2);
    	adView3 = (AdView)findViewById(R.id.adview3);
    	
    	// 1. 광고 클릭시 실행할 리스너
    	adView.setOnAdClickedListener(new OnAdClickedListener(){
    		@Override
    		public void OnAdClicked(){
    			Log.i("RRT",  "광고를 클릭했습니다.");
    		}
    	});
    	
    	// 2. 광고 내려받기 실패했을 경우에 실행할 리스너
    	adView.setOnAdFailedListener(new OnAdFailedListener() {
    		@Override
    		public void OnAdFailed(AdError error, String message) {
    			Log.w("RRT", message);
    		}
    	});
    	
    	// 3. 광고를 정상적으로 내려받았을 경우에 실행할 리스너
    	adView.setOnAdLoadedListener(new OnAdLoadedListener() {
	    	@Override
	    	public void OnAdLoaded() {
	    		Log.i("RRT", "광고가 정상적으로 로딩되었습니다.");
	    	}
    	});
    	// 4. 광고를 불러올때 실행할 리스너
    	adView.setOnAdWillLoadListener(new OnAdWillLoadListener() {
	    	@Override
	    	public void OnAdWillLoad(String url) {
	    		Log.i("RRT", "광고를 불러옵니다. : " + url);
	    	}
    	});
    	
    	adView.setAnimationType(AnimationType.FLIP_HORIZONTAL);
    	adView.setVisibility(View.VISIBLE);
    	adView2.setAnimationType(AnimationType.FLIP_HORIZONTAL);
    	adView2.setVisibility(View.VISIBLE);
    	adView3.setAnimationType(AnimationType.FLIP_HORIZONTAL);
    	adView3.setVisibility(View.VISIBLE);
    }
    
    private void loadingDialog(){
    	progressDialog = new ProgressDialog(this);    	
    	progressDialog.setMessage("로딩 중입니다...");
    	progressDialog.show();
    }
        
    private void tab1_play(){
    	    	
    	llLoad = (RelativeLayout)findViewById(R.id.llLoad);
    	llTop_tab1 = (LinearLayout)findViewById(R.id.llTop_tab1);
    	    	
    	wheel = new U2Wheeling(this);    	    	
    	showLoadButton();
    	    	
    	Button btnMakeRingtones = (Button)findViewById(R.id.btnMakeRingtones);
    	btnMakeRingtones.setOnClickListener(showMakeRingtonesDialog);
    	
    	Button btnShare = (Button)findViewById(R.id.btnShare);
    	btnShare.setOnClickListener(showShareDialog);    	
    }
        
    private void tab2_ranking(){
    	btnRankingLike = (Button)findViewById(R.id.btnPopulFavolite);
    	btnRankingLike.setOnClickListener(refreshRankingHandler);
    	
    	btnRankingSave = (Button)findViewById(R.id.btnPopulRingtone);
    	btnRankingSave.setOnClickListener(refreshRankingHandler);
    	    	
    	setRankingList("save");
    }
    
    private void setRankingList(String type){
    	if(type=="save"){
    		btnRankingSave.setBackgroundResource(R.drawable.txt_mostsaved_on);
			btnRankingLike.setBackgroundResource(R.drawable.txt_mostliked);
    	}else{
    		btnRankingSave.setBackgroundResource(R.drawable.txt_mostsaved);
			btnRankingLike.setBackgroundResource(R.drawable.txt_mostliked_on);
    	}
    	
    	ListView lvRank = (ListView)findViewById(R.id.lvRanklist);    	
    	RankListAdapter adapter = new RankListAdapter(this, R.layout.rankinglist_item, alRanking, type, this);
    	adapter.notifyDataSetChanged();
    	lvRank.setAdapter(adapter);   	
    	
    	if(progressDialog != null) progressDialog.dismiss();
    }
    
    private View.OnClickListener refreshRankingHandler = new View.OnClickListener() {
		
		public void onClick(View v) {
			loadingDialog();
			
			switch(v.getId()){
			case R.id.btnPopulFavolite:
				getJsonObj(RANKINGS_PATH+"?type=like&range=all");		
				break;
			case R.id.btnPopulRingtone:
				getJsonObj(RANKINGS_PATH+"?type=save&range=all");				
				break;
			}			
		}
	};
    
    private void tab3_mybox(){
    	btnMySaveRingtones = (Button)findViewById(R.id.btnMyboxRingtone);
    	btnMySaveRingtones.setOnClickListener(refreshMyboxHandler);
    	
    	btnMyLikeRingtones = (Button)findViewById(R.id.btnMyboxFavolite);
    	btnMyLikeRingtones.setOnClickListener(refreshMyboxHandler);
    	
    	setMyboxList("save");
    }
    
    private void setMyboxList(String type){
    	if(type=="like"){
    		btnMyLikeRingtones.setBackgroundResource(R.drawable.txt_liked_on);
			btnMySaveRingtones.setBackgroundResource(R.drawable.txt_saved);
    	}else{
    		btnMySaveRingtones.setBackgroundResource(R.drawable.txt_saved_on);
			btnMyLikeRingtones.setBackgroundResource(R.drawable.txt_liked);
    	}
    	
    	ListView lvMybox = (ListView)findViewById(R.id.lvMyboxlist);
    	MyboxListAdapter adapter = new MyboxListAdapter(this, R.layout.myboxlist_item, alMybox, type, this);
    	adapter.notifyDataSetChanged();
    	lvMybox.setAdapter(adapter);
    	
    	if(progressDialog != null) progressDialog.dismiss();
    }
    
    
    private View.OnClickListener refreshMyboxHandler = new View.OnClickListener() {
		
		public void onClick(View v) {
			loadingDialog();
			
			switch(v.getId()){
			case R.id.btnMyboxRingtone:
				getJsonObj(MYBOX_SAVE_PATH+"?user_token="+mUserToken);
				break;
			case R.id.btnMyboxFavolite:
				getJsonObj(MYBOX_LIKE_PATH+"?user_token="+mUserToken);
				break;
			}			
		}
	};
    
    /////////////////////////TAB1//////////////////////////////////
    
    private void showLoadButton(){
    	    	
    	if(llLoad.getChildCount()!=0){
    		likeButton.setVisibility(View.GONE);
    		nextButton.setVisibility(View.GONE);
    		llLoad.removeView(wheel);
    	}
    		
    	llTop_tab1.setVisibility(View.INVISIBLE);
    	            	
    	wheel.setCanDraw(false);
    	RelativeLayout.LayoutParams wrap_params1 = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
    	wrap_params1.setMargins(-5, 0, 0, 0);
    	llLoad.addView(wheel, wrap_params1);
    	
    	mBtnLoad = new Button(this);    	
		mBtnLoad.setBackgroundResource(R.drawable.btnload_off);
		RelativeLayout.LayoutParams wrap_params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    	wrap_params2.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
		mBtnLoad.setLayoutParams(wrap_params2);		
		mBtnLoad.setOnClickListener(btnLoadClickListener);		
		llLoad.addView(mBtnLoad);
    }
    
    private View.OnClickListener btnLoadClickListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			if(!isLoading){				
				onLoading();
			}						
		}
	};
	
	private void onLoading(){
		isLoading = true;
		mBtnLoad.setBackgroundResource(R.drawable.btnload_on);
		wheel.setCanDraw(true);
		wheel.startLoading();
		
		mThread = new Thread(new Runnable(){
			public void run(){
				startStream();
			}
		});
		mThread.start();
									
//		imgCircleOn = new ImageView(mContext);
//		imgCircleOn.setBackgroundResource(R.drawable.circle_on);
//		RelativeLayout.LayoutParams wrap_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//		imgCircleOn.setLayoutParams(wrap_params);
//		llLoad.addView(imgCircleOn);
//		loadplaybtn.start();
//		loadAnimation();
	}
    
    public void showPlayButton(){
    	if(!isFirstPlay){
    		initAdam();
    		isFirstPlay = true;
    	}
    	
    	isLoading = false;
    	wheel.stopLoading();
    	    	
    	TabWidget tabs = (TabWidget)findViewById(android.R.id.tabs);
    	tabs.setVisibility(View.VISIBLE);    	
    	    	
    	ImageView imgPushthis = (ImageView)findViewById(R.id.imgPushthis);    	
    	imgPushthis.setVisibility(View.INVISIBLE);
        	
    	LinearLayout llDetailText = (LinearLayout)findViewById(R.id.llDetailText);    	
    	llDetailText.setVisibility(View.VISIBLE);
    	llDetailText.bringToFront();
    	
    	//Like / dislike icon    	    	    	  	
    	likeButton = (ImageView)findViewById(R.id.imgLikeButton);
    	likeButton.setVisibility(View.VISIBLE);    	
    	likeButton.setOnClickListener(likeButtonClickListener);
    	
    	nextButton = (ImageView)findViewById(R.id.imgNextButton);
    	nextButton.setVisibility(View.VISIBLE);
    	nextButton.setOnClickListener(nextButtonClickListener);
    	    	
    	mBtnLoad.setBackgroundResource(R.drawable.btnload_on);
    	mBtnLoad.setOnClickListener(imgRecordClickListener);    		   		   	
       
        songPlay();
    }
    
    private View.OnClickListener likeButtonClickListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			setLogs("like");			
			likeButton.setVisibility(View.INVISIBLE);
			
			//startAnimation(0);
			
	    	llTop_tab1.setVisibility(View.VISIBLE);
		}
	};
	
	private View.OnClickListener nextButtonClickListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			
			getJsonObj(MIXES_PATH);
			initPlayTab();
		}
	};
    
    private View.OnClickListener imgRecordClickListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			songPlay();			
		}
	};
	
	private View.OnClickListener showMakeRingtonesDialog = new View.OnClickListener() {
		
		public void onClick(View v) {
			mrDialog = new MakeRingtonesDialog(mContext, mActivity);
			mrDialog.show();
		}
	};
		
	private View.OnClickListener showShareDialog = new View.OnClickListener() {
		
		public void onClick(View v) {
			ShareRingtonesDialog dlog = new ShareRingtonesDialog(mContext, mixes_id);
			dlog.show();
		}
	};
    
	private void initPlayTab(){
		isPlay = false;
		
		if(mp != null){
			if(mp.isPlaying()){
				mp.stop();
				mp.release();
			}
		}
		
		//if(isFirstPlay) showLoadButton();
		showLoadButton();
		onLoading();
	}
	
	public void startStream(){
		this.isPrepare = false;
		
		setLogs("play");
		
		thistime = System.currentTimeMillis();
		
		String path = this.mixes_path+".mp3";
				
		mp = new MediaPlayer();
        mp.setOnBufferingUpdateListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnPreparedListener(this);  
		
        try{		       
        	mp.setAudioStreamType(AudioManager.STREAM_MUSIC);	
        	mp.setDataSource(path);
	        mp.prepare();
	               		        	       		      
        }catch(Exception e){
        	Log.e("error", e.getMessage());
        	startStream();
        }   
	}
	
	private void getJsonObj(String path){
				
		AsyncStringDownloader.download(path, new AsyncStringDownloader.OnCompletedListener() {
			
			public void onErrorRaised(String url, Exception e) {
				// TODO Auto-generated method stub
				Log.e("error", e.getMessage());
				
			}
			
			public void onCompleted(String url, String result) {
				try {
										
					if(url==MIXES_PATH){
												
						JSONObject jo = new JSONObject(result);
						mixes_id = jo.getString("id");					
						mixes_path = jo.getString("src");
						mixes_play_count = jo.getString("play_count");
						mixes_save_count = jo.getString("save_count");
						mixes_like_count = jo.getString("like_count");
						mixes_share_count = jo.getString("share_count");	
						mixes_reptitle = jo.getString("rep_title");
						JSONArray jo_titles = jo.getJSONArray("titles");
						int jo_titles_size = jo_titles.length();
						mixes_titles_cnt = new String[jo_titles_size];
						mixes_titles_title = new String[jo_titles_size];
						
						for(int i=0; i<jo_titles_size; i++){
							mixes_titles_cnt[i] = jo_titles.getJSONObject(i).getString("cnt");
							mixes_titles_title[i] = jo_titles.getJSONObject(i).getString("title");
						}
						
						TextView tvSongName = (TextView)findViewById(R.id.tvSongName);
						tvSongName.setText(mixes_reptitle);
															    	
				    	TextView tvDownNum = (TextView)findViewById(R.id.tvDownNum);
				    	tvDownNum.setText(mixes_save_count);
				    	
				    	TextView tvLike = (TextView)findViewById(R.id.tvLike);
				    	tvLike.setText(mixes_like_count);
				    	
				    	TextView tvShare = (TextView)findViewById(R.id.tvShare);
				    	tvShare.setText(mixes_share_count);				    					    	
				    	
					}else if(url.contains(RANKINGS_PATH)){						
											
						int i = 0;
						JSONObject jo = new JSONObject(result);
						
						JSONArray jo_mixes = jo.getJSONArray("mixes");
						int size = jo_mixes.length();												
											
						alRanking.clear();
						
						for(i=0; i< size; i++){														
							
							RRTData rd = new RRTData();
							rd.date = jo_mixes.getJSONObject(i).getString("date");
							rd.type = jo_mixes.getJSONObject(i).getString("type");
							rd.range = jo_mixes.getJSONObject(i).getString("range");
							rd.rank = jo_mixes.getJSONObject(i).getString("rank");
							rd.gap = jo_mixes.getJSONObject(i).getString("gap");
							rd.mix_id = jo_mixes.getJSONObject(i).getString("mix_id");
							rd.updated_at = jo_mixes.getJSONObject(i).getString("updated_at");	
							rd.play_count = jo_mixes.getJSONObject(i).getString("play_count");
							rd.save_count = jo_mixes.getJSONObject(i).getString("save_count");
							rd.like_count = jo_mixes.getJSONObject(i).getString("like_count");
							rd.share_count = jo_mixes.getJSONObject(i).getString("share_count");
							rd.rep_title = jo_mixes.getJSONObject(i).getString("rep_title");
							rd.src = jo_mixes.getJSONObject(i).getString("src");
							
							JSONArray jo_titles = jo_mixes.getJSONObject(i).getJSONArray("titles");
							int jo_titles_size = jo_titles.length();
							rd.titles_cnt = new String[jo_titles_size];
							rd.titles_title = new String[jo_titles_size];
							
							for(int j=0; j<jo_titles_size; j++){
								rd.titles_cnt[j] = jo_titles.getJSONObject(j).getString("cnt");
								rd.titles_title[j] = jo_titles.getJSONObject(j).getString("title");
							}
							
							alRanking.add(rd);
						}					
						
						if(url.contains("like")){
							setRankingList("like");
						}else{
							setRankingList("save");
						}
												
					}else{
						
						int i = 0;
						JSONObject jo = new JSONObject(result);
						
						JSONArray jo_mixes = jo.getJSONArray("mixes");
						int size = jo_mixes.length();
						
						alMybox.clear();
						
						for(i=0; i<size; i++){
							RRTData rd = new RRTData();
							rd.logged_at = jo_mixes.getJSONObject(i).getString("logged_at");							
							rd.mix_id = jo_mixes.getJSONObject(i).getString("id");								
							rd.play_count = jo_mixes.getJSONObject(i).getString("play_count");
							rd.save_count = jo_mixes.getJSONObject(i).getString("save_count");
							rd.like_count = jo_mixes.getJSONObject(i).getString("like_count");
							rd.share_count = jo_mixes.getJSONObject(i).getString("share_count");
							rd.rep_title = jo_mixes.getJSONObject(i).getString("rep_title");
							rd.src = jo_mixes.getJSONObject(i).getString("src");
							
							JSONArray jo_titles = jo_mixes.getJSONObject(i).getJSONArray("titles");
							int jo_titles_size = jo_titles.length();
							rd.titles_cnt = new String[jo_titles_size];
							rd.titles_title = new String[jo_titles_size];
							
							for(int j=0; j<jo_titles_size; j++){
								rd.titles_cnt[j] = jo_titles.getJSONObject(j).getString("cnt");
								rd.titles_title[j] = jo_titles.getJSONObject(j).getString("title");
							}
							
							alMybox.add(rd);
						}
						
						if(url.contains(MYBOX_SAVE_PATH)){
							setMyboxList("save");
						}else{
							setMyboxList("like");
						}
					}
					
				} catch (JSONException e) {					
					e.printStackTrace();
				}
			}
		});		
		
	}
	
	public void setLogs(String type){		
		String path = this.LOGS_PATH + "write?type="+type+ "&user_token="+mUserToken+"&mix_id="+mixes_id;
	//save/share/like/play		
		try {
			URL url = new URL(path);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));			
			in.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	public void songPlay(){
		if(!isPlay && isPrepare){
			mp.start();
			isPlay = true;
			wheel.startPlaying();
			//startAnimation();
			mBtnLoad.setBackgroundResource(R.drawable.btnload_on);			
		}else{
			mp.pause();
			mp.seekTo(0);			
			isPlay = false;
			wheel.stopPlaying();
			//stopAnimation();
			mBtnLoad.setBackgroundResource(R.drawable.btnload_off);
		}
	}  

	public void goPlayTab(RRTData rd){
		thMain.setCurrentTab(1);
		
		mixes_id = rd.mix_id;
		mixes_path = rd.src;
		mixes_play_count = rd.play_count;
		mixes_save_count = rd.save_count;
		mixes_like_count = rd.like_count;
		mixes_share_count = rd.share_count;
		mixes_reptitle = rd.rep_title;
		mixes_titles_cnt = rd.titles_cnt;
		mixes_titles_title = rd.titles_title;
		
		TextView tvSongName = (TextView)findViewById(R.id.tvSongName);
		tvSongName.setText(mixes_reptitle);
		  	
    	TextView tvDownNum = (TextView)findViewById(R.id.tvDownNum);
    	tvDownNum.setText(mixes_save_count);
    	
    	TextView tvLike = (TextView)findViewById(R.id.tvLike);
    	tvLike.setText(mixes_like_count);
    	
    	TextView tvShare = (TextView)findViewById(R.id.tvShare);
    	tvShare.setText(mixes_share_count);
    	
    	initPlayTab();
	}
    
	///MediaPlayer
    public void onBufferingUpdate(MediaPlayer arg0, int percent){    	
    	float progress = ((float)arg0.getCurrentPosition()*percent/arg0.getDuration());   	
    	//pb.setProgress((int)(progress*100));
    }
    
    public void onCompletion(MediaPlayer arg0){    	
    	songPlay();
    }
    
    public void onPrepared(MediaPlayer mediaplayer){
    	
    	this.isPrepare = true;
    	showPlayButton();
    	System.out.println(System.currentTimeMillis() - thistime);
    }            
   
    private void startAnimation(int type){
    	//playAnim = new RotateAnimation(0,360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
    	playAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 10, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
    	
    	playAnim.setRepeatMode(1);
    	playAnim.setRepeatCount(0);
    	playAnim.setInterpolator(new LinearInterpolator());
        playAnim.setAnimationListener(new AnimationListener(){
      
        	long duration = 20000;
        	public void onAnimationEnd(Animation arg0){
        		
        	}
        	
        	public void onAnimationRepeat(Animation arg0){  
    			        		
        	}
        	
        	public void onAnimationStart(Animation arg0){        		       
        		arg0.setDuration(duration);
        	}
        });
        likeButton.startAnimation(playAnim);        
    }
    
    private void stopAnimation(){
    	playAnim.setRepeatCount(0);
    	playAnim.setAnimationListener(new AnimationListener(){
    		
    		long duration = 500;
    		public void onAnimationEnd(Animation arg0){
    			
    		}
    		
    		public void onAnimationRepeat(Animation arg0){

    		}
    		
    		public void onAnimationStart(Animation arg0){
    				arg0.setDuration(duration);
    		}
    	});
    	
    	imgCircleOn.setAnimation(playAnim);    	
    }
    
    public void setRingtone(String title){
    	
    	mrDialog.dismiss();
    	progressDialog = new ProgressDialog(this);    	
    	progressDialog.setMessage("벨소리를 생성중입니다...");
    	progressDialog.show();
    	progressThread = new ProgressThread(handler, title, mContext);
    	progressThread.start();   	    	  
    
    }
            
    final Handler handler = new Handler(){
    	public void handleMessage(Message msg){
    		Boolean isComplete = msg.getData().getBoolean("isComplete");
    		
    		if(isComplete){
    			progressDialog.dismiss();
    			progressThread.setState(ProgressThread.STATE_DONE);
    			Toast toast = Toast.makeText(mContext, "벨소리를 성공적으로 생성하였습니다.", Toast.LENGTH_SHORT);
    			toast.show();   
    			
    			setLogs("save");
    		}
    	}
    };
    
    private class ProgressThread extends Thread{
    	Handler mHandler;    	
    	String mTitle;
    	Context mContext;
    	final static int STATE_DONE = 0;
    	final static int STATE_RUNNING = 1;
    	int mState;
    	Boolean isComplete = false;
    	
    	ProgressThread(Handler h, String title, Context context){
    		mHandler = h;
    		mTitle = title;
    		mContext = context;
    	}
    	
    	public void run(){
    		mState = STATE_RUNNING;
    		isComplete = false;    		    		
    		    			
    		try{
        		String downUrl = mixes_path+".mp3";
        		
        		File file;
            	String filePath = null;
        		
        		 String ext = Environment.getExternalStorageState();    		 
        		 if(ext.equals(Environment.MEDIA_MOUNTED)){
        			filePath = Environment.getExternalStorageDirectory().getAbsolutePath(); 
        		 }else{
        			filePath = Environment.getDataDirectory().getAbsolutePath(); 
        		 }				
        		        		 	       		 
        		 
        		 filePath += "/"+mTitle+".mp3";
        		 InputStream is = new URL(downUrl).openStream();
        		 BufferedInputStream bis = new BufferedInputStream(is);
        		 
        		 
        		 file = new File(filePath);
        		 FileOutputStream out = new FileOutputStream(file);        		 
        		 saveRemoteFile(bis, out);
        		 out.close();
        		         		         		
        		 ContentValues values = new ContentValues();
        	     values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        	     values.put(MediaStore.MediaColumns.TITLE, mTitle);
        	     values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        	     values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        	     
        	     Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
        	     Uri newUri = getContentResolver().insert(uri, values);
        	     RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE, newUri);
        	     
        	     
        	}catch(Exception e){
        		e.printStackTrace();
        	}        	    		    			
			    			    			
    		isComplete = true;
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putBoolean("isComplete", isComplete);
			msg.setData(b);
			mHandler.sendMessage(msg);      		
    	}
    	
    	private void saveRemoteFile(BufferedInputStream bis, FileOutputStream fos) throws IOException
        {
        	int c = 0;
       	        
        	ByteArrayBuffer baf = new ByteArrayBuffer(4096);       			
        	
        	while((c = bis.read()) != -1)  baf.append((byte)c);       		 	       		 	
        	
        	fos.write(baf.toByteArray());
        	fos.flush();
        	fos.close();
        }
    	
    	public void setState(int state){
    		mState = state;
    	}    	
    	
    }
    
    
    private void kakaolink(){
		 String strMessage = "카카오링크를 사용하여 메세지를 전달해 보세요."; 
			String strURL = "http://link.kakao.com";
			String strAppId = "com.kakao.android.image";
			String strAppVer = "2.0";
			String strAppName = "[카카오톡]"; 
			KakaoLink link;
			try {
			        link = new KakaoLink(this, strURL, strAppId, strAppVer, strMessage, strAppName,"UTF-8");
			        if( link.isAvailable() ) {
			            startActivity(link.getIntent());
			        } 
			} catch (Exception e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			}			 
		
	 }
 
    /////////For C2DM PUSH
    private void pushC2DM(){
    	
    	Intent registIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
    	registIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
    	registIntent.putExtra("sender", "korea651@gmail.com");
    }
    
    public void onReceive(Context context, Intent intent){
    	if(intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")){
    		handleRegistration(context, intent);
    	}else if(intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")){
    		handleMessage(context, intent);
    	}
    }
    
    private void handleRegistration(Context context, Intent intent){
    	String registration = intent.getStringExtra("registration_id");
    	if(intent.getStringExtra("error") != null){
    		
    	}else if(intent.getStringExtra("unregistered") != null){
    		
    	}else if(registration != null){
    		
    	}
    }
    
    private void handleMessage(Context context, Intent intent){
    	String accountName = intent.getExtras().getString("AccountName");
    	String message = intent.getExtras().getString("MESSAGE");
    	
    }
    
    ////////////////////////TAB2//////////////////////////////////////////////////
    
    
    
    
    ///////////////////////TAB3//////////////////////////////////////////////////
    
    
    @Override    
    public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu);
    	
    	MenuItem menuItem = menu.add(0,COPYRIGHT_MENU_ID, 0, "Copyright");
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	super.onOptionsItemSelected(item);
    	
    	switch(item.getItemId()){
    	case COPYRIGHT_MENU_ID:
    		showCopyrightDialog();
    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    private void showCopyrightDialog(){
    	CopyrightDialog dlog = new CopyrightDialog(mContext);
    	dlog.show();
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	
    	if(isPlay) songPlay();
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	
    	if(mp!=null){
    		mp.stop();
    		mp.release();
    	}
    	
    	if(adView != null){
    		adView.destroy();
    		adView = null;    		
    	}
    	if(adView2 != null){
    		adView2.destroy();
    		adView2 = null;
    	}
    	if(adView3 != null){
    		adView3.destroy();
    		adView3 = null;
    	}
    }
}


