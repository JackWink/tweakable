package com.jackwink.tweakable.generators.java;

import android.preference.Preference;

import com.jackwink.tweakable.controls.NumberPickerPreference;
import com.jackwink.tweakable.types.TweakableInteger;

/**
 *
 */
public class IntegerPreferenceBuilder extends BaseBuilder<Preference> {
    @Override
    public Class[] getHandledTypes() {
        return new Class[] { Integer.class, int.class };
    }

    @Override
    public Preference build() {
        NumberPickerPreference preference = build(NumberPickerPreference.class, true);
        preference.setDialogMessage((String) getRequiredAttribute(BUNDLE_SUMMARY_KEY));
        preference.setWraps(true);
        preference.setMaxValue((Integer)
                getRequiredAttribute(TweakableInteger.BUNDLE_MAX_VALUE_KEY));
        preference.setMinValue((Integer)
                getRequiredAttribute(TweakableInteger.BUNDLE_MIN_VALUE_KEY));
        return preference;
    }
}
