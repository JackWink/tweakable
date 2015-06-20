package com.jackwink.tweakabledemo;

import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jackwink.tweakable.controls.TweaksFragment;
import com.jackwink.tweakable.generators.java.PreferenceCategoryBuilder;
import com.jackwink.tweakable.generators.java.PreferenceScreenBuilder;
import com.squareup.seismic.ShakeDetector;


public class MainActivity extends PreferenceActivity implements ShakeDetector.Listener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //hearShake();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void hearShake() {
        Log.i(TAG, "Shake!");
        //TweaksFragment fragment = new TweaksFragment();

        Bundle screenResources = new Bundle();
        screenResources.putString(PreferenceScreenBuilder.EXTRA_TITLE, "My Settings!");
        PreferenceScreen screen = new PreferenceScreenBuilder()
                .setContext(this)
                .setPreferenceManager(getPreferenceManager())
                .setBundle(screenResources)
                .build();

        Bundle categoryResources = new Bundle();
        categoryResources.putString(PreferenceCategoryBuilder.BUNDLE_TITLE_KEY, "Boolean");
        categoryResources.putString(PreferenceCategoryBuilder.BUNDLE_KEYATTR_KEY, "boolean-category");
        PreferenceCategory category = new PreferenceCategoryBuilder()
                .setContext(this)
                .setBundle(categoryResources)
                .build();
        screen.addPreference(category);
        setPreferenceScreen(screen);
    }

}
