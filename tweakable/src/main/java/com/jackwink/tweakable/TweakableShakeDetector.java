package com.jackwink.tweakable;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;

import com.squareup.seismic.ShakeDetector;

import java.lang.ref.WeakReference;

/**
 *
 */
public final class TweakableShakeDetector implements ShakeDetector.Listener {

    private WeakReference<Context> mContext;

    private static ShakeDetector mShakeDetector;

    private boolean mHeardShake = false;

    protected TweakableShakeDetector(Context context) {
        mContext = new WeakReference<Context>(context);
        mShakeDetector = new ShakeDetector(this);
    }

    public synchronized void reset() {
        mHeardShake = false;
    }

    public synchronized boolean listen() {
        if (!mHeardShake && mContext.get() != null) {
            SensorManager manager =
                    (SensorManager) mContext.get().getSystemService(Context.SENSOR_SERVICE);
            mShakeDetector.start(manager);
            return true;
        }
        return false;
    }

    @Override
    public synchronized void hearShake() {
        if (!mHeardShake && mContext.get() != null) {
            mHeardShake = true;
            Intent settingsIntent = new Intent(mContext.get(), TweaksActivity.class);
            settingsIntent.putExtra(TweaksActivity.EXTRA_SHOW_FRAGMENT,
                    TweaksFragment.class.getCanonicalName());
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.get().startActivity(settingsIntent);
        }
    }
}
