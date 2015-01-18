package com.android.rrt.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

public class RRTUserToken {
	private Context cntx;
	private String user_token;
	private static RRTUserToken rrtUserToken;
	
	public static RRTUserToken getInstance(Context cntx) {
		if( rrtUserToken == null ) {
			rrtUserToken = new RRTUserToken(cntx);
		}
		
		return rrtUserToken;
	}
	
	private RRTUserToken(Context cntx) {
		this.cntx = cntx;
	}
	
	public String getUserToken() {
		if( user_token == null ) {
			try {
		    	SharedPreferences pref = cntx.getSharedPreferences("RandomRingtones", Context.MODE_WORLD_READABLE);
		    	
		    	if( pref.contains("user_token") ) {
		    		user_token = pref.getString("user_token", "");
				}
				else {
					user_token = makeSHA1Hash( getDeviceId() );
					SharedPreferences.Editor edit = pref.edit();
					edit.putString("user_token", user_token);
					edit.commit();
				}
				return user_token;
			} catch(Exception e) {
				return null;
			}
		} else {
			return user_token;
		}
    }
	
	public String getDeviceId() {
		TelephonyManager telephonyManager;
		telephonyManager = (TelephonyManager) cntx.getSystemService(Context.TELEPHONY_SERVICE);
		System.out.println("deviceid: " + telephonyManager.getDeviceId());
		return telephonyManager.getDeviceId();
	}
	
	public static String makeSHA1Hash(String s){
		MessageDigest m = null;

		String hash = null;
		try {
			m = MessageDigest.getInstance("SHA-1");
			m.update(s.getBytes(),0,s.length());
			hash = new BigInteger(1, m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return hash;
	}
		
	public static String makeMD5Hash(String s) {
		MessageDigest m = null;

		String hash = null;
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(),0,s.length());
			hash = new BigInteger(1, m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return hash;
	}
}
