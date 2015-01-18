package com.u2likeus.android.rrt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class U2Wheeling extends ImageView {
		
		protected Adapter adapter;
		boolean canDraw = true;
		Paint paint;
		LinearLayout linear1;
		Handler hAnim;
		
		float spacing = 5;
				
		long frame_delay = 25;
		float acc = 0.14f;
		
		float currentDegree = 0;
		float currentVelocity = 0;
		float velocity = 0;
		
		int overWheelAlpha = 255;
		int currentOverWheelAlpha = 0;
		
		int viewWidth = 0;
		int viewHeight = 0;
		
		Bitmap overWheel;
		Bitmap underWheel;
		
		final int MAX_VELOCITY = 5;
		final int MIN_VELOCITY = 0;
		
		public U2Wheeling(Context context) {
			super(context);
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			linear1 = new LinearLayout(context);
			hAnim = new Handler();
			
			overWheel = BitmapFactory.decodeResource(getResources(), R.drawable.circle_on);
			underWheel = BitmapFactory.decodeResource(getResources(), R.drawable.circle_off);
			
			hAnim.post(runEnterframe);
		}
		
		public void startPlaying() {
			stopLoading();
			setVelocity(MAX_VELOCITY);
		}
		
		public void stopPlaying() {
			setVelocity(MIN_VELOCITY);
		}
		
		public void startLoading() {
			stopPlaying();
			hAnim.post(runLoading);
		}
		
		public void stopLoading() {
			overWheelAlpha = 255;
			hAnim.removeCallbacks(runLoading);
		}
		
		protected void setVelocity(float velocity) {
			this.velocity = velocity;
		}
		
		public float getVelocity() {
			return this.velocity;
		}
		
		public void setCanDraw(boolean can){
			canDraw = can;		
		}
		
		Runnable runLoading = new Runnable() {
			@Override
			public void run() {
				if( overWheelAlpha == 255 ) {
					overWheelAlpha = 0;
				} else {
					overWheelAlpha = 255;
				}
				
				hAnim.postDelayed(runLoading, 1000);
			}
		};
		
		Runnable runEnterframe = new Runnable() {
			@Override
			public void run() {
				currentVelocity += (velocity - currentVelocity)*acc;
				currentDegree += currentVelocity;
				if(currentDegree > 360.0f ) {
					currentDegree -= 360.0f;
				}
				
				if( (int)overWheelAlpha != (int)currentOverWheelAlpha ) {
					currentOverWheelAlpha += (overWheelAlpha - currentOverWheelAlpha)*acc;
				}
				
				invalidate();
				hAnim.postDelayed(runEnterframe, frame_delay);
			}
		};
					
		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);
			if( canDraw ) {
				_onDraw(canvas);
			}
		}
		
		protected void _onDraw(Canvas canvas) {
			Matrix matrix = new Matrix();
			matrix.setRotate(currentDegree, overWheel.getWidth()/2, overWheel.getHeight()/2);
			matrix.postScale(0.8f, 0.8f);
			matrix.postTranslate(45.0f, 1.2f);
			paint.setAlpha(currentOverWheelAlpha);
			canvas.drawBitmap(overWheel, matrix, paint);
			paint.setAlpha((int) Math.floor(currentVelocity/MAX_VELOCITY*255));
			canvas.drawBitmap(underWheel, matrix, paint);			
		}
}