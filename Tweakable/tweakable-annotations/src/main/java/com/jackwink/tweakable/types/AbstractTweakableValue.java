package com.jackwink.tweakable.types;

import android.os.Bundle;

/**
 *
 */
public abstract class AbstractTweakableValue<T> implements TweakableValue<T> {
    public static final String BUNDLE_DEFAULT_VALUE_KEY = "defaultsTo";
    public static final String BUNDLE_KEYATTR_KEY = "key";
    public static final String BUNDLE_TITLE_KEY = "title";
    public static final String BUNDLE_SUMMARY_KEY = "summary";
    public static final String BUNDLE_TYPEINFO_KEY = "type_information";

    public static final String BUNDLE_CATEGORY_KEY = "category";
    public static final String BUNDLE_SCREEN_KEY = "screen";

    public static final String ROOT_SCREEN_KEY = "tweakable-values-root-screen";
    public static final String ROOT_CATEGORY_KEY = "tweakable-values-root-category";


    protected String mTitle;
    protected String mKey;
    protected String mSummary;
    protected String mCategory;
    protected String mScreen;

    @Override
    public String getKey() {
        return mKey;
    }

    @Override
    public String getTitle() {
        return mTitle == null ? "ERROR" : mTitle;
    }

    @Override
    public String getSummary() {
        return mSummary;
    }

    @Override
    public String getCategory() {
        return mCategory;
    }

    @Override
    public String getScreen() {
        return mScreen;
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEYATTR_KEY, mKey);
        bundle.putString(BUNDLE_TITLE_KEY, mTitle);
        bundle.putString(BUNDLE_SUMMARY_KEY, mSummary);
        bundle.putString(BUNDLE_TYPEINFO_KEY, getType().getName());
        bundle.putString(BUNDLE_CATEGORY_KEY, getCategory());
        bundle.putString(BUNDLE_SCREEN_KEY, getScreen());
        return bundle;
    }

    @Override
    public PreferenceEntry[] getChildren() {
        return null;
    }

    public static String getDefaultString(String value, String defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }
}