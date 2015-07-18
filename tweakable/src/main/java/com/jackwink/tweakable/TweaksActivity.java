package com.jackwink.tweakable;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;

/**
 *
 */
public class TweaksActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected boolean isValidFragment(String fragment) {
        return fragment.equals("com.jackwink.tweakable.TweaksFragment");
    }

    @Override
    protected void onDestroy() {
        Tweakable.startShakeListener();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
