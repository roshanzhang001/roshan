package com.android.mumassistant.conductor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.sax.StartElementListener;
import android.telephony.SmsMessage;

public class locationReceiver extends BroadcastReceiver{
	String lat = null;
	String lon = null;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())){
			SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(context);
			String oldnum = shp.getString("ctrlnum", "");
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			for (Object pdu : pdus) {
				SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
				String address = message.getOriginatingAddress();
				String content = message.getMessageBody();
				if((address.indexOf(oldnum)>=1)&&(content.substring(0,4).equals("**XY"))){
					String[] ss = content.split("#");
					for (int i = 0; i < ss.length; i++) {
						if(ss[i].indexOf("lat") >= 0){
							lat = ss[i].substring(4, ss[i].length());
						}
						if(ss[i].indexOf("lon") >= 0){
							lon = ss[i].substring(4, ss[i].length());
						}
					}
					
					if((lat != null)&&(lon != null)){
						Intent locationintent = new Intent();
						Bundle locationbundle = new Bundle();
						locationbundle.putString("lat", lat);
						locationbundle.putString("lon", lon);
						locationintent.putExtras(locationbundle);
						locationintent.setClass(context, locationactivity.class);
						locationintent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(locationintent);
					}
				}
			}
			
		}
	}

}
