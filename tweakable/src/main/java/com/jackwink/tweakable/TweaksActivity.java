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
    protected void onStop() {
        if (Tweakable.isShakeListenerEnabled()) {
            Tweakable.resetShakeListener();
            Tweakable.startShakeListener();
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
