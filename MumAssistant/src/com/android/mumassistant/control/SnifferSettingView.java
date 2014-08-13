package com.android.mumassistant.control;



import com.android.mumassistant.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;

public class SnifferSettingView extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sniffersetting);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
