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
    public static final String BUNDLE_TYPEINFO_KEY = "type_information";

    public static final String BUNDLE_CATEGORY_KEY = "category";


    protected String mTitle;
    protected String mKey;
    protected String mSummary;
    protected String mCategory;

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
    public String getCategory() {
        return mCategory;
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEYATTR_KEY, mKey);
        bundle.putString(BUNDLE_TITLE_KEY, mTitle);
        bundle.putString(BUNDLE_SUMMARY_KEY, mSummary);
        bundle.putString(BUNDLE_TYPEINFO_KEY, getType().getName());
        if (mCategory != null && !mCategory.isEmpty()) {
            bundle.putString(BUNDLE_CATEGORY_KEY, mCategory);
        }
        return bundle;
    }

    @Nullable
    @Override
    public PreferenceEntry[] getChildren() {
        return null;
    }
}
