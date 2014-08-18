package com.android.mumassistant.conductor;

import com.android.mumassistant.R;
import com.android.mumassistant.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ConductorActivity extends Activity {
	
	private int mWifi_value = 0;
	private int mData_value = 0;
	private int mPayment_value = 0;
	private int mLocation_value = 0;
	
	private Switch mWifi_ctrl;
	private Switch mData_ctrl;
	private Switch mPayment_ctrl;
	private Switch mLocation_ctrl;
	private Button mSend_btn;
	private EditText mTel_num;
	private SharedPreferences shp;
	SmsManager smsManager;
	
	private OnCheckedChangeListener WifiOnCheckChangeListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				mWifi_value = 1;
			}else{
				mWifi_value = 0;
			}
		}
		
	};
	
	private OnCheckedChangeListener DataOnCheckChangeListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				mData_value = 1;
			}else{
				mData_value = 0;
			}
		}
		
	};
	
	private OnCheckedChangeListener PaymentOnCheckChangeListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				mPayment_value = 1;
			}else{
				mPayment_value = 0;
			}
		}
		
	};

	private OnCheckedChangeListener LocationOnCheckChangeListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				mLocation_value = 1;
			}else{
				mLocation_value = 0;
			}
		}
		
	};
	
	private OnClickListener SendOnCheckedChangeListener = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(shp == null){
				return;
			}
			String oldnum = shp.getString(Utils.CTRLNUM, "");
			String telnum = mTel_num.getText().toString().trim();
			
			if(!oldnum.equals(mTel_num.getText().toString())){
				Editor editor = shp.edit();
				editor.putString(Utils.CTRLNUM, telnum);
				editor.commit();
			}
			String message = Utils.TARGET + Utils.WIFITAG + mWifi_value + Utils.DATATAG + 
					mData_value + Utils.PAYTAG + mPayment_value + Utils.LOCATIONTAG + mLocation_value;
			
			if((null != telnum)&&(null != message)){
				smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(telnum, null, message, null, null);
			}
		}
		
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_conductor);
          
        mWifi_ctrl = (Switch)findViewById(R.id.switch_wifi);
        mWifi_ctrl.setOnCheckedChangeListener(WifiOnCheckChangeListener);
        
        mData_ctrl = (Switch)findViewById(R.id.switch_data);
        mData_ctrl.setOnCheckedChangeListener(DataOnCheckChangeListener);
        
        mPayment_ctrl = (Switch)findViewById(R.id.switch_payment_query);
        mPayment_ctrl.setOnCheckedChangeListener(PaymentOnCheckChangeListener);
        
        mLocation_ctrl = (Switch)findViewById(R.id.switch_location);
        mLocation_ctrl.setOnCheckedChangeListener(LocationOnCheckChangeListener);
        
        mSend_btn = (Button)findViewById(R.id.button_send);
        mSend_btn.setOnClickListener(SendOnCheckedChangeListener);
        
		shp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String oldnum = shp.getString(Utils.CTRLNUM, "");
		mTel_num = (EditText)findViewById(R.id.edit_tel);
		mTel_num.setText(oldnum);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.add(0, 1, 0, R.string.location_menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	 switch (item.getItemId()) {
    	 	case 1:
    	 		Intent locationView = new Intent();
    	 		locationView.setClass(this, LocationActivity.class);
    	 		startActivity(locationView);
    	 		break;
    	 	default:
    	 		break;
    	 }
		return false;
    	
    }
    
}
