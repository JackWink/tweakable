package com.jackwink.tweakable.generators.java;
;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceScreenException;

/**
 * Builds a {@link PreferenceScreen}
 */
public class PreferenceScreenBuilder extends BaseBuilder<PreferenceScreen> {
    private static final String TAG = PreferenceScreenBuilder.class.getSimpleName();

    public static final String EXTRA_TITLE = "title";
    private static final String DEFAULT_TITLE = "Settings";

    private PreferenceManager mPreferenceManager = null;


    public PreferenceScreenBuilder() {
        mAttributeMap = new Bundle();
    }

    @Override
    public PreferenceScreenBuilder setContext(Context context) {
        mContext = context;
        return this;
    }

    @Override
    public PreferenceScreenBuilder setBundle(Bundle map) {
        mAttributeMap = map;
        return this;
    }

    public PreferenceScreenBuilder setPreferenceManager(PreferenceManager preferenceManager) {
        mPreferenceManager = preferenceManager;
        return this;
    }

    /** {@inheritDoc} */
    public PreferenceScreen build() {
        if (mContext == null) {
            throw new FailedToBuildPreferenceScreenException("Failed to set context before building!");
        }

        if (mPreferenceManager == null) {
            throw new FailedToBuildPreferenceScreenException(
                    "Failed to set preference manager before building!");
        }

        PreferenceScreen screen = mPreferenceManager.createPreferenceScreen(mContext);
        screen.setTitle(mAttributeMap.getString(EXTRA_TITLE, DEFAULT_TITLE));
        return screen;
    }
}
