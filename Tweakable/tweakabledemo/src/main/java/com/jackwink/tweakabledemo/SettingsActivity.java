package com.jackwink.tweakabledemo;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;

/**
 *
 */
public class SettingsActivity extends PreferenceActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //hearShake();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}
