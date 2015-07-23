package com.jackwink.tweakable.generators.java;

import android.preference.Preference;

import com.jackwink.tweakable.controls.ActionPreference;

import java.lang.reflect.Method;

/**
 *
 */
public class ActionPreferenceBuilder extends BaseBuilder<Preference> {

    @Override
    public Class[] getHandledTypes() {
        return new Class[] { Method.class };
    }

    @Override
    public Preference build() {
        return build(ActionPreference.class, true);
    }
}
