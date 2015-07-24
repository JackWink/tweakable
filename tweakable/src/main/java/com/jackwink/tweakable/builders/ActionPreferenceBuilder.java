package com.jackwink.tweakable.builders;

import android.preference.Preference;

import com.jackwink.tweakable.controls.ActionPreference;

import java.lang.reflect.Method;

/**
 *
 */
@SuppressWarnings({ "rawtypes" })
public class ActionPreferenceBuilder extends BasePreferenceBuilder<Preference> {

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Method.class };
    }

    @Override
    public Preference build() {
        return build(ActionPreference.class, true);
    }
}
