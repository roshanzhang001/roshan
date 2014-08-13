package com.android.mumassistant.conductor;

import com.android.mumassistant.R;

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

public class conductoractivity extends Activity {
	
	private static int wifi_value = 0;
	private static int data_value = 0;
	private static int payment_value = 0;
	private static int location_value = 0;
	
	private final String target = "##";
	private final String wifi_tag = "#wifi_";
	private final String data_tag = "#data_";
	private final String payment_tag = "#payment_";
	private final String location_tag = "#location_";

	private Switch wifi_ctrl;
	private Switch data_ctrl;
	private Switch payment_ctrl;
	private Switch location_ctrl;
	private Button send_btn;
	private EditText tel_num;
	SmsManager smsManager;
	
	private OnCheckedChangeListener WifiOnCheckChangeListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				wifi_value = 1;
			}else{
				wifi_value = 0;
			}
		}
		
	};
	
	private OnCheckedChangeListener DataOnCheckChangeListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				data_value = 1;
			}else{
				data_value = 0;
			}
		}
		
	};
	
	private OnCheckedChangeListener PaymentOnCheckChangeListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				payment_value = 1;
			}else{
				payment_value = 0;
			}
		}
		
	};

	private OnCheckedChangeListener LocationOnCheckChangeListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				location_value = 1;
			}else{
				location_value = 0;
			}
		}
		
	};
	
	private OnClickListener SendOnCheckedChangeListener = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String oldnum = shp.getString("ctrlnum", "");
			String telnum = tel_num.getText().toString().trim();
			
			if(!oldnum.equals(tel_num.getText().toString())){
				Editor editor = shp.edit();
				editor.putString("ctrlnum", telnum);
				editor.commit();
			}
			String message = target + wifi_tag + wifi_value + data_tag + 
					data_value + payment_tag + payment_value + location_tag + location_value;
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
        wifi_ctrl = (Switch)findViewById(R.id.switch_wifi);
        data_ctrl = (Switch)findViewById(R.id.switch_data);
        payment_ctrl = (Switch)findViewById(R.id.switch_payment_query);
        location_ctrl = (Switch)findViewById(R.id.switch_location);
        send_btn = (Button)findViewById(R.id.button_send);
        tel_num = (EditText)findViewById(R.id.edit_tel);
        
        wifi_ctrl.setOnCheckedChangeListener(WifiOnCheckChangeListener);
        data_ctrl.setOnCheckedChangeListener(DataOnCheckChangeListener);
        payment_ctrl.setOnCheckedChangeListener(PaymentOnCheckChangeListener);
        location_ctrl.setOnCheckedChangeListener(LocationOnCheckChangeListener);
        
        send_btn.setOnClickListener(SendOnCheckedChangeListener);
        
		SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String oldnum = shp.getString("ctrlnum", "");
		tel_num.setText(oldnum);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.add(0, 1, 0, "location"); 
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	 switch (item.getItemId()) {
    	 	case 1:
    	 		Intent locationcheck = new Intent();
    	 		locationcheck.setClass(this, locationactivity.class);
    	 		startActivity(locationcheck);
    	 		break;
    	 	default:
    	 		break;
    	 }
		return false;
    	
    }
    
}
