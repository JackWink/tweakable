package com.jackwink.tweakable.generators.java;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceCategory;

import com.jackwink.tweakable.types.AbstractTweakableValue;

import java.util.Map;

/**
 * Builds a {@link PreferenceCategory} from a {@link Bundle}
 */
public class PreferenceCategoryBuilder extends BaseBuilder<PreferenceCategory> {
    private static final String TAG = PreferenceCategoryBuilder.class.getSimpleName();

    public PreferenceCategoryBuilder() {
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[0];
    }

    /** {@inheritDoc} */
    @Override
    public PreferenceCategory build() {
        PreferenceCategory category = new PreferenceCategory(mContext);
        category.setKey((String) getRequiredAttribute(BUNDLE_KEYATTR_KEY));
        category.setTitle((CharSequence) getRequiredAttribute(BUNDLE_TITLE_KEY));
        return category;
    }
}
