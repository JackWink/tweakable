package com.jackwink.tweakable.generators.java;

import android.preference.Preference;

import com.jackwink.tweakable.controls.SliderPreference;
import com.jackwink.tweakable.types.TweakableFloat;

/**
 *
 */
public class FloatPreferenceBuilder extends BaseBuilder<Preference> {

    @Override
    public Class[] getHandledTypes() {
        return new Class[] { Float.class, float.class };
    }

    @Override
    public Preference build() {
        SliderPreference preference = build(SliderPreference.class, true);
        preference.setDialogMessage((String) getRequiredAttribute(BUNDLE_SUMMARY_KEY));
        preference.setMaxValue((Float)
                getRequiredAttribute(TweakableFloat.BUNDLE_MAX_VALUE_KEY));
        preference.setMinValue((Float)
                getRequiredAttribute(TweakableFloat.BUNDLE_MIN_VALUE_KEY));
        return null;
    }
}
