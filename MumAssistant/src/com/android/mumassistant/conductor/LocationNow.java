package com.android.mumassistant.conductor;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocationNow {
	private static final String TAG = "LocationNow";
	private LocationClient mLocationClient = null;
	private double mLatitude = 0;
	private double mLongitude = 0;
	
	private location_thread mLocationThread = new location_thread();
	
	private class location_thread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Log.v(TAG,"location thread run");
			mLocationClient.start();
			mLocationClient.requestLocation();

			while((mLatitude < 0.1)&&(mLongitude < 0.1)){
				try {
					sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			Log.v(TAG,"zjm thread ok");
			if((mLatitude != 0)&&(mLongitude != 0)){
				mLocationClient.stop();
				this.interrupt();
				Log.v(TAG,"zjm thread stop");
			}
		}
	}
	/*
	public LocationNow(Context context){
		mContext = context;
	}
	*/
	public void getLocation(Context context){
		if(context == null){
			return;
		}
		mLocationClient = new LocationClient(context);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setProdName("MumAssistant");
		option.setScanSpan(5000);
		option.setServiceName("com.baidu.location.service");
		mLocationClient.setLocOption(option);

		mLocationClient.registerLocationListener(new BDLocationListener() {
			

			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					Log.v(TAG,"BD location null");
					return;
				}
				mLatitude = location.getLatitude();
				mLongitude = location.getLongitude();
				Log.v(TAG,"zjm mLatitude :"+mLatitude+"--"+"mLongitude :"+mLongitude);
				SetLatitudeLongitude(mLatitude,mLongitude);
			}

			@Override
			public void onReceivePoi(BDLocation arg0) {
				// TODO Auto-generated method stub
				
			}
		
		});
		if(!mLocationThread.isAlive()){
			mLocationThread.start();
		}
		
	}
	
	public double GetLatitude(){
		return this.mLatitude;
	}

	public void SetLatitudeLongitude(double lat,double lon){
		this.mLatitude = lat;
		this.mLongitude = lon;
	}

	public double GetLongitude(){
		return this.mLongitude;
	}
}

