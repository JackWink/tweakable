package com.jackwink.tweakable.generators.java;

import android.content.Context;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceScreenException;
import com.jackwink.tweakable.types.AbstractTweakableValue;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds a {@link PreferenceScreen}
 */
public class PreferenceScreenBuilder extends BaseBuilder<PreferenceScreen> {
    private static final String TAG = PreferenceScreenBuilder.class.getSimpleName();

    private PreferenceManager mPreferenceManager = null;


    public PreferenceScreenBuilder() {
        mAttributeMap = new LinkedHashMap<String, Object>();
    }

    @Override
    public PreferenceScreenBuilder setContext(Context context) {
        mContext = context;
        return this;
    }

    @Override
    public PreferenceScreenBuilder setBundle(Map<String, Object> map) {
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
        screen.setTitle((String) getRequiredAttribute(AbstractTweakableValue.BUNDLE_TITLE_KEY));
        screen.setKey((String) getRequiredAttribute(AbstractTweakableValue.BUNDLE_KEYATTR_KEY));
        return screen;
    }
}
