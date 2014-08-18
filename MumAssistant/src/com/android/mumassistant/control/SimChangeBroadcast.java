package com.android.mumassistant.control;

import com.android.mumassistant.R;
import com.android.mumassistant.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class SimChangeBroadcast  extends BroadcastReceiver {

	private SharedPreferences mSp;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mSp = PreferenceManager.getDefaultSharedPreferences(context);
		TelephonyManager telephonyManager = (TelephonyManager) context.
				getSystemService(Context.TELEPHONY_SERVICE);
		String currentSim = telephonyManager.getSimSerialNumber();
		String protectSim = mSp.getString(Utils.OLDSIM, null);
		if((protectSim == null)&&(currentSim != null)){
			Editor editor = mSp.edit();
			editor.putString(Utils.OLDSIM, currentSim);
			editor.commit();
		}
		if((protectSim != null)&&(!currentSim.equals(protectSim))){
			SmsManager smsManager = SmsManager.getDefault();
			String number  = mSp.getString(Utils.CTRLNUM, null);
			if(number !=null){
				smsManager.sendTextMessage(number, null, 
						context.getResources().getString(R.string.sim_change), null, null);
			}
		}
	}

}
