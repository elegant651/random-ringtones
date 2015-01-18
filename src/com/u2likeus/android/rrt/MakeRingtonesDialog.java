package com.u2likeus.android.rrt;

import com.android.rrt.data.RRTData;
import com.u2likeus.android.rrt.MyboxListAdapter.ViewHolder;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MakeRingtonesDialog extends Dialog {

	public MakeRingtonesDialog(Context context, RandomRingtonesActivity activity){
		super(context);
		final RandomRingtonesActivity mActivity = activity;
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.make_ringtone_dialog);		
						
		String[] titles_cnt = activity.mixes_titles_cnt;
		String[] titles_title = activity.mixes_titles_title;
		
		final EditText etName = (EditText)findViewById(R.id.etRingtoneName);
		etName.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				etName.setText("");				
				return false;
			}
		});
		etName.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
			}
		});
		
		ListView lvSongNames = (ListView)findViewById(R.id.lvSongNames);
		RingtonesNamesAdapter adapter = new RingtonesNamesAdapter(context, R.layout.songnamelist_item, titles_cnt, titles_title, etName);
		adapter.notifyDataSetChanged();
    	lvSongNames.setAdapter(adapter);			
				
		Button btnSave = (Button)findViewById(R.id.btnSaveRingtone);
		btnSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				mActivity.setRingtone(etName.getText().toString());
			}
		});				
		
	}
	
}

class RingtonesNamesAdapter extends BaseAdapter {
	
	LayoutInflater mInflater;
	int mLayout;
	String[] mTitles_cnt;
	String[] mTitles_title;
	EditText mEtName;
	
	public RingtonesNamesAdapter(Context context, int alayout, String[] titles_cnt, String[] titles_title, EditText etName){
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = alayout;
		mTitles_cnt = titles_cnt;
		mTitles_title = titles_title;
		mEtName = etName;
	}
	
	public int getCount(){
		return mTitles_cnt.length;
	}
	
	public String getItem(int position){
		return mTitles_title[position];
	}
	
	public long getItemId(int position){
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		final int pos = position;
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(mLayout, parent, false);
			
			holder.tvSongName = (TextView)convertView.findViewById(R.id.tvOtherSongName);			
			holder.rlSongName = (LinearLayout)convertView.findViewById(R.id.llSongNameList_Row);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
								
		holder.tvSongName.setTextColor(Color.YELLOW);
		holder.tvSongName.setText("¢Ü "+ mTitles_title[position]);
				
		final LinearLayout rlSongName = holder.rlSongName;
		
		rlSongName.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				mEtName.setText(mTitles_title[pos]);
				//rlSongName.setBackgroundColor(Color.BLUE);
			}
		});
						
		return convertView;
	}
	
	static class ViewHolder{		
		TextView tvSongName;
		TextView tvSongCnt;
		LinearLayout rlSongName;
	}
}
