package com.android.mumassistant.conductor;

import com.android.mumassistant.PureSwitch;
import com.android.mumassistant.PureSwitch.OnChangeListener;
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
	private int mShortcut_value = 0;
	
	private PureSwitch mWifi_ctrl;
	private PureSwitch mData_ctrl;
	private PureSwitch mPayment_ctrl;
	private PureSwitch mLocation_ctrl;
	private PureSwitch mShortcut_ctrl;
	private Button mSend_btn;
	private EditText mTel_num;
	private SharedPreferences shp;
	SmsManager smsManager;
	
	private OnChangeListener WifiOnCheckChangeListener = new OnChangeListener(){

		@Override
		public void onChange(PureSwitch arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				mWifi_value = 1;
			}else{
				mWifi_value = 0;
			}
		}
		
	};
	
	private OnChangeListener DataOnCheckChangeListener = new OnChangeListener(){

		@Override
		public void onChange(PureSwitch arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				mData_value = 1;
			}else{
				mData_value = 0;
			}
		}
		
	};
	
	private OnChangeListener PaymentOnCheckChangeListener = new OnChangeListener(){

		@Override
		public void onChange(PureSwitch arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				mPayment_value = 1;
			}else{
				mPayment_value = 0;
			}
		}
		
	};

	private OnChangeListener LocationOnCheckChangeListener = new OnChangeListener(){

		@Override
		public void onChange(PureSwitch arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				mLocation_value = 1;
			}else{
				mLocation_value = 0;
			}
		}
		
	};
	
	private OnChangeListener ShortcutOnCheckChangeListener = new OnChangeListener(){

		@Override
		public void onChange(PureSwitch arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				mShortcut_value = 1;
			}else{
				mShortcut_value = 0;
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
					mData_value + Utils.PAYTAG + mPayment_value 
					+ Utils.LOCATIONTAG + mLocation_value +Utils.SHORTCUTTAG + mShortcut_value;
			
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
          
        mWifi_ctrl = (PureSwitch)findViewById(R.id.switch_wifi);
        mWifi_ctrl.setOnChangeListener(WifiOnCheckChangeListener);
        
        mData_ctrl = (PureSwitch)findViewById(R.id.switch_data);
        mData_ctrl.setOnChangeListener(DataOnCheckChangeListener);
        
        mPayment_ctrl = (PureSwitch)findViewById(R.id.switch_payment_query);
        mPayment_ctrl.setOnChangeListener(PaymentOnCheckChangeListener);
        
        mLocation_ctrl = (PureSwitch)findViewById(R.id.switch_location);
        mLocation_ctrl.setOnChangeListener(LocationOnCheckChangeListener);
        
        mShortcut_ctrl = (PureSwitch)findViewById(R.id.switch_shortcut);
        mShortcut_ctrl.setOnChangeListener(ShortcutOnCheckChangeListener);
        
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
	menu.add(0, 2, 0, R.string.downloadshow);
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
		case 2:
			Intent downloadView = new Intent();
    	 		downloadView.setClass(this, DownloadShow.class);
    	 		startActivity(downloadView);
			break;
    	 	default:
    	 		break;
    	 }
		return false;
    	
    }
    
}
