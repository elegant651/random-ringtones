package com.u2likeus.android.rrt;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.rrt.data.RRTData;

public class MyboxListAdapter extends BaseAdapter {
	ArrayList<RRTData> mAlMybox = new ArrayList<RRTData>();
	LayoutInflater mInflater;
	int mLayout;
	String mType;
	Context mContext;
	RandomRingtonesActivity mActivity;
	
	public MyboxListAdapter(Context context, int alayout, ArrayList<RRTData> alMybox, String type, RandomRingtonesActivity activity){		
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mLayout = alayout;	
		mAlMybox = alMybox;
		mType = type;
		mActivity = activity;
	}
	
	public int getCount(){
		return mAlMybox.size();
	}
	
	public String getItem(int position){
		return mAlMybox.get(position).rep_title;
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
			holder.tvSongName = (TextView)convertView.findViewById(R.id.tvMyboxitemName);
			holder.rlMyboxRow = (LinearLayout)convertView.findViewById(R.id.rlMyboxRow);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
						
		
		RRTData md = mAlMybox.get(position);
			
		holder.tvSongName.setText(md.rep_title);	
		
		holder.rlMyboxRow.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				System.out.println("click:"+pos);
				
				mActivity.goPlayTab(mAlMybox.get(pos));				
			}
		});
		
		return convertView;
	}
	
	static class ViewHolder{		
		TextView tvDate;
		TextView tvSongName;		
		LinearLayout rlMyboxRow;
	}
}
