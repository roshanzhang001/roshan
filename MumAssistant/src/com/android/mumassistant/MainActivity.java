package com.android.mumassistant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {
	private String[] mListTitle = null; 
	private String[] mListStr = null;
	ListView mListView = null;
	ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();
	Intent conductorintent = new Intent("com.android.mumassistant.conductor.conductoractivity");
	Intent controlintent = new Intent("com.android.mumassistant.control.SnifferSettingView");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView)findViewById(R.id.enter_list);
        mListTitle = getApplicationContext().getResources().getStringArray(R.array.enter_list);
        mListStr = getApplicationContext().getResources().getStringArray(R.array.enter_list_summary);
        int lengh = mListTitle.length;
        for(int i =0; i < lengh; i++) {  
            Map<String,Object> item = new HashMap<String,Object>();
            if(i == 1){
            	item.put("image", R.drawable.child); 
            }else{
            	item.put("image", R.drawable.mum); 
            }
            item.put("title", mListTitle[i]);  
            item.put("text", mListStr[i]);  
            mData.add(item);   
        }
        
        SimpleAdapter adapter = new SimpleAdapter(this,mData,R.xml.enter_list_view,  
                new String[]{"image","title","text"},new int[]{R.id.image,R.id.title,R.id.text});
        mListView.setAdapter(adapter); 
        
        mListView.setOnItemClickListener(new OnItemClickListener() {  
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(arg2 == 0){
					startActivity(controlintent);
				}else if(arg2 == 1){
					startActivity(conductorintent);
				}
			}  
        });  

        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
