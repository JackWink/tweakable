package com.jackwink.tweakable.builders;

import android.os.Bundle;
import android.preference.PreferenceCategory;

/**
 * Builds a {@link PreferenceCategory} from a {@link Bundle}
 */
@SuppressWarnings({ "rawtypes" })
public class PreferenceCategoryBuilder extends BasePreferenceBuilder<PreferenceCategory> {
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
