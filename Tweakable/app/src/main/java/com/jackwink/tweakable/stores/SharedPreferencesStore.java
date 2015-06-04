package com.jackwink.tweakable.stores;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesStore implements ValueStore {
    private static final String TAG = SharedPreferencesStore.class.getSimpleName();
    private static final String STORE_NAME = "Tweakable-" + TAG;

    private static SharedPreferencesStore mInstance;

    private static SharedPreferences mSharedPreferences;

    SharedPreferencesStore(Context context) {
        mSharedPreferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesStore instance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferencesStore(context);
        }
        return mInstance;
    }

    public boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    public int getInt(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        if (!editor.commit()) {
            throw new RuntimeException("Editor failed to save values!");
        }
    }
}
