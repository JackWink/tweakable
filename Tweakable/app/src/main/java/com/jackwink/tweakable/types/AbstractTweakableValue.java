package com.jackwink.tweakable.types;

import android.os.Bundle;

import android.support.annotation.Nullable;
/**
 *
 */
public abstract class AbstractTweakableValue<T> implements TweakableValue<T> {
    public static final String BUNDLE_DEFAULT_VALUE_KEY = "defaultsTo";
    public static final String BUNDLE_KEYATTR_KEY = "key";
    public static final String BUNDLE_TITLE_KEY = "title";
    public static final String BUNDLE_SUMMARY_KEY = "summary";

    protected String mTitle;
    protected String mKey;
    protected String mSummary;

    @Override
    public String getKey() {
        return mKey;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSummary() {
        return mSummary;
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEYATTR_KEY, mKey);
        bundle.putString(BUNDLE_TITLE_KEY, mTitle);
        bundle.putString(BUNDLE_SUMMARY_KEY, mSummary);
        return bundle;
    }

    @Nullable
    @Override
    public PreferenceEntry[] getChildren() {
        return null;
    }
}
