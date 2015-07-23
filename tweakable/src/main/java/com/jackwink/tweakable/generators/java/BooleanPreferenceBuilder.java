package com.jackwink.tweakable.generators.java;

import android.preference.Preference;
import android.preference.SwitchPreference;

import com.jackwink.tweakable.types.TweakableBoolean;


/**
 *
 */
public class BooleanPreferenceBuilder extends BaseBuilder<Preference> {
    private static final String TAG = PreferenceFactory.class.getSimpleName();

    public BooleanPreferenceBuilder() {
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] { Boolean.class, boolean.class };
    }

    /**
     * Builds a SwitchPreference object
     */
    @Override
    public Preference build() {
        SwitchPreference preference = null;
        preference = build(SwitchPreference.class, true);
        preference.setChecked((boolean) mDefaultValue);
        preference.setSummaryOn((String)
                getOptionalAttribute(TweakableBoolean.BUNDLE_ON_SUMMARY_KEY));
        preference.setSummaryOff((String)
                getOptionalAttribute(TweakableBoolean.BUNDLE_OFF_SUMMARY_KEY));
        preference.setSwitchTextOn((String)
                getOptionalAttribute(TweakableBoolean.BUNDLE_ON_LABEL_KEY));
        preference.setSwitchTextOff((String)
                getOptionalAttribute(TweakableBoolean.BUNDLE_OFF_LABEL_KEY));
        return preference;
    }
}
