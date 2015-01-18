package com.u2likeus.android.rrt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;

public class LoadPlayButton extends View {

	Context mContext;
	RandomRingtonesActivity mActivity;
	Paint mPaint;
	RectF mOval;
	float sweepAngle = 0;
	private Drawable drCircleOff;
	private Animation mLoadanim;
	//ShapeDrawable mRound;
	
	private Handler mHandler;
	Thread mThread;
	
	public LoadPlayButton(Context context, RandomRingtonesActivity activity){
		super(context);
		mContext = context;
		mActivity = activity;
			
		//mRound = new ShapeDrawable(new OvalShape());		
		//mRound.getPaint().setColor(Color.BLUE);
		//mRound.setBounds(120, 100, 330, 310);
		
		drCircleOff = context.getResources().getDrawable(R.drawable.circle_off);
		drCircleOff.setBounds(0,0,drCircleOff.getIntrinsicWidth()-20,drCircleOff.getIntrinsicHeight()-20);		
				
		//mLoadanim = AnimationUtils.loadAnimation(context, R.anim.rotate_djrecord);										
		//mDrawable = new AnimateDrawable(drCircleOff, mLoadanim);
		
		
//		mPaint = new Paint();
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(20);
//        mPaint.setColor(Color.GREEN);        
//        mOval = new RectF(100, 80, 350, 330);
        
//        mHandler = new Handler();
        mThread = new Thread(new Runnable(){
			public void run(){
				mActivity.startStream();
			}
		});
	}
	
	public void start(){	
		
		//mLoadanim.startNow();
				
		//mHandler.post(mTimerRunnable);
				
		mThread.start();		
	}
	
	public void stop(){	
		
//		mHandler.removeCallbacks(mTimerRunnable);
//		
		if(mThread!=null && mThread.isAlive())
			mThread.interrupt();	
	}
		
//	private Runnable mTimerRunnable = new Runnable(){
//		public void run(){
//						
//			postInvalidate();
//			
//			mHandler.postDelayed(mTimerRunnable, 120);
//		}
//	};
	
	@Override
	protected void onDraw(Canvas canvas){
//		mRound.draw(canvas);
					
		drCircleOff.draw(canvas);
		//invalidate();
		
//		if(sweepAngle!=360){
//			sweepAngle += 20;
//	    	canvas.drawArc(mOval, 0, sweepAngle, false, mPaint);
//		}else{
//			if(mActivity.isPrepare){	
//				sweepAngle = 0;
//				stop();				
//				mActivity.showPlayButton();
//			}
//		}
	}
		
	
//	public boolean onTouch(View v, MotionEvent event){
//		int action = event.getAction();
//		if(action != MotionEvent.ACTION_UP) return true;
//		
//		float x = event.getX();
//		float y = event.getY();
//		
//		Rect mRoundRect = mRound.getBounds();
//		if(x>=mRoundRect.left && x<mRoundRect.right && y>=mRoundRect.top &&y<mRoundRect.bottom){
//			if(!isLoading){				
//				start();
//			}						
//		}
//		
//		return true;
//	}	
	
}
