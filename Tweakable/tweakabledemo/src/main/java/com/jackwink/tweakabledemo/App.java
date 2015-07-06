package com.jackwink.tweakabledemo;

import android.app.Application;

import com.jackwink.tweakable.Tweakable;

/**
 *
 */
public class App extends Application {
    private static App mInstance;

    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Tweakable.init(this);
    }
}
