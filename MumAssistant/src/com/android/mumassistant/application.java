package com.android.mumassistant;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class application extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(this);
		
	}

}
