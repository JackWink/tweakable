package com.jackwink.tweakable.generators.java;

import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;

import com.jackwink.tweakable.types.TweakableString;

/**
 *
 */
public class StringPreferenceBuilder extends BaseBuilder<Preference> {

    @Override
    public Class[] getHandledTypes() {
        return new Class[] { String.class };
    }

    @Override
    public Preference build() {
        Preference preference = null;
        String[] options = (String[]) getRequiredAttribute(TweakableString.BUNDLE_OPTIONS_KEY);
        if (options.length != 0) {
            preference = build(ListPreference.class, true);
            ((ListPreference) preference).setValue((String) mDefaultValue);
            ((ListPreference) preference).setEntries((String[])
                    getRequiredAttribute(TweakableString.BUNDLE_OPTIONS_KEY));
            ((ListPreference) preference).setEntryValues((String[])
                    getRequiredAttribute(TweakableString.BUNDLE_OPTIONS_KEY));
        } else {
            preference = build(EditTextPreference.class, true);
            ((EditTextPreference) preference).setText((String) mDefaultValue);
        }

        return preference;
    }
}
