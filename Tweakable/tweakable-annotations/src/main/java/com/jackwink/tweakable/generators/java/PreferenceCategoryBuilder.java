package com.jackwink.tweakable.generators.java;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceCategory;

import com.jackwink.tweakable.types.AbstractTweakableValue;

/**
 * Builds a {@link PreferenceCategory} from a {@link Bundle}
 */
public class PreferenceCategoryBuilder extends BaseBuilder<PreferenceCategory> {
    private static final String TAG = PreferenceCategoryBuilder.class.getSimpleName();

    public static final String BUNDLE_TITLE_KEY = AbstractTweakableValue.BUNDLE_TITLE_KEY;
    public static final String BUNDLE_KEYATTR_KEY = AbstractTweakableValue.BUNDLE_KEYATTR_KEY;

    public PreferenceCategoryBuilder() {
    }

    /** {@inheritDoc} */
    @Override
    public PreferenceCategoryBuilder setBundle(Bundle attributeMap) {
        mAttributeMap = attributeMap;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public PreferenceCategoryBuilder setContext(Context context) {
        mContext = context;
        return this;
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
