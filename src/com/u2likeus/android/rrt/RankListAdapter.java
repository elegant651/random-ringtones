package com.u2likeus.android.rrt;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.rrt.data.RRTData;

public class RankListAdapter extends BaseAdapter {
	ArrayList<RRTData> mAlRanking;
	LayoutInflater mInflater;
	int mLayout;
	String mType;
	RandomRingtonesActivity mActivity;
	
	/////http://atin.tistory.com/379
	
	public RankListAdapter(Context context, int alayout, ArrayList<RRTData> alRanking, String type, RandomRingtonesActivity activity){
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = alayout;		
		mAlRanking = alRanking;
		mType = type;
		mActivity = activity;
	}
	
	public int getCount(){
		return mAlRanking.size();
	}
	
	public String getItem(int position){
		return mAlRanking.get(position).rep_title;
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
			holder.tvIdx = (TextView)convertView.findViewById(R.id.tvRankitemIdx);
			holder.tvSongName = (TextView)convertView.findViewById(R.id.tvRankitemName);				
			holder.rlRankingRow = (LinearLayout)convertView.findViewById(R.id.rlRankingRow);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
				
		RRTData rd = mAlRanking.get(position);

		holder.tvIdx.setText(rd.rank);
		holder.tvSongName.setText(rd.rep_title);
		
//		if(mType=="like"){
//			holder.tvSongCount.setText(rd.like_count);
//		}else{
//			holder.tvSongCount.setText(rd.save_count);
//		}						
		
		holder.rlRankingRow.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				System.out.println("click:"+pos);
				
				mActivity.goPlayTab(mAlRanking.get(pos));
			}
		});
				
		return convertView;
	}
	
	static class ViewHolder{
		TextView tvIdx;
		TextView tvSongName;
//		TextView tvSongCount;		
		LinearLayout rlRankingRow;
	}

}
