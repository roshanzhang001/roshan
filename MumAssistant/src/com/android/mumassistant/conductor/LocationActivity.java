package com.android.mumassistant.conductor;

import com.android.mumassistant.R;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;

public class LocationActivity extends Activity implements
OnGetGeoCoderResultListener{
	
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	BaiduMap mBaiduMap = null;
	MapView mMapView = null;
	private EditText mLat_edit;
	private EditText mLon_edit;
	private EditText mEditCity;
	private EditText mEditGeoCodeKey;
	
	public void SearchButtonProcess(View v) {
		if (v.getId() == R.id.reversegeocode) {
			String lat_str = mLat_edit.getText().toString();
			String lon_str = mLon_edit.getText().toString();
			
			if(lat_str == null || lon_str == null 
					||(lat_str.length() == 0)
					||(lon_str.length() == 0)){
				return;
			}
			LatLng ptCenter = new LatLng((Float.valueOf(lat_str)), 
					(Float.valueOf(lon_str)));
			// 反Geo搜索
			mSearch.reverseGeoCode(new ReverseGeoCodeOption()
					.location(ptCenter));
		} else if (v.getId() == R.id.geocode) {
			String editCity_str = mEditCity.getText().toString();
			String editGeoCodeKey_str = mEditGeoCodeKey.getText().toString();
			if(editCity_str == null || editGeoCodeKey_str == null 
					||(editCity_str.length() == 0)
					||(editGeoCodeKey_str.length() == 0)){
				return;
			}
			// Geo搜索
			mSearch.geocode(new GeoCodeOption().city(
					editCity_str).address(
							editGeoCodeKey_str));
		}
	}
	
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(LocationActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		String strInfo = String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);
		Toast.makeText(LocationActivity.this, strInfo, Toast.LENGTH_LONG).show();
		mLat_edit.setText((result.getLocation().latitude)+"");
		mLon_edit.setText((result.getLocation().longitude)+"");
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(LocationActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		Toast.makeText(LocationActivity.this, result.getAddress(),
				Toast.LENGTH_LONG).show();
		
		String[] ss = result.getAddress().split("市");
		mEditCity.setText(ss[0]);
		mEditGeoCodeKey.setText(ss[1]);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locationactivity);
		CharSequence titleLable = "地理查找功能";
		setTitle(titleLable);
		mEditCity = (EditText) findViewById(R.id.city);
		mEditGeoCodeKey = (EditText) findViewById(R.id.geocodekey);
		mLat_edit = (EditText) findViewById(R.id.lat);
		mLon_edit = (EditText) findViewById(R.id.lon);
		
		Bundle bundle=getIntent().getExtras();
		if(bundle != null){
			String lat=bundle.getString("lat");
			String lon=bundle.getString("lon");
			if((lat != null) && (lon != null)){
				mLat_edit.setText(lat);
				mLon_edit.setText(lon);
			}
		}
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		LatLng cenpt = new LatLng(29.806651,121.606983); 
		MapStatus mMapStatus = new MapStatus.Builder()
		.target(cenpt).zoom(18).build();
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		mBaiduMap.setMapStatus(mMapStatusUpdate);

		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//mMapView.onDestroy();
		mSearch.destroy();
		super.onDestroy();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.add(0, 1, 0, R.string.location_now);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	 switch (item.getItemId()) {
    	 	case 1:
    	 		LocationNow locationnow = new LocationNow(getApplicationContext());
    	 		locationnow.getLocation();
    	 		/*
    	 		 Dialog alertDialog = new AlertDialog.Builder(this).
    	 			    setTitle(R.string.location_alert_title).
    	 			    setMessage(R.string.location_alert_message).
    	 			    setIcon(R.drawable.ic_launcher).
    	 			    create();
    	 			  alertDialog.show();
    	 		*/
    	 		while((locationnow.GetLatitude()== 0)&&(locationnow.GetLongitude()== 0)){
    	 			
    	 		}
			
			if((locationnow.GetLatitude()!= 0)&&(locationnow.GetLongitude()!= 0)){
			
    			LatLng ptCenter = new LatLng((locationnow.GetLatitude()), 
    					(locationnow.GetLongitude()));
    			// 反Geo搜索
    			mSearch.reverseGeoCode(new ReverseGeoCodeOption()
    					.location(ptCenter));
    			}
    	 		//alertDialog.dismiss();	
    	 		break;
    	 	default:
    	 		break;
    	 }
		return false;
    	
    }

}
