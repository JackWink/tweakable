package com.jackwink.tweakable.generators.java;

import android.content.Context;
import android.preference.PreferenceCategory;

import java.util.LinkedHashMap;

/**
 *
 */
public class PreferenceCategoryBuilder extends BaseBuilder<PreferenceCategory> {
    private static final String TAG = PreferenceCategoryBuilder.class.getSimpleName();

    public static final String TITLE_ATTRIBUTE = "title";
    public static final String KEY_ATTRIBUTE = "key";

    public PreferenceCategoryBuilder() {
    }

    @Override
    public PreferenceCategoryBuilder setAttributeMap(LinkedHashMap<String, Object> attributeMap) {
        mAttributeMap = attributeMap;
        return this;
    }

    @Override
    public PreferenceCategoryBuilder setContext(Context context) {
        mContext = context;
        return this;
    }

    /** {@inheritDoc} */
    public PreferenceCategory build() {
        PreferenceCategory category = new PreferenceCategory(mContext);
        category.setKey((String) getRequiredAttribute(KEY_ATTRIBUTE));
        category.setTitle((CharSequence) getRequiredAttribute(TITLE_ATTRIBUTE));
        return category;
    }
}
