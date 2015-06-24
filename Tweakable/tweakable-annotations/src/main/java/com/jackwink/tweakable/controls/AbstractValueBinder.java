package com.jackwink.tweakable.controls;


import android.preference.Preference;

import java.lang.reflect.Field;

/**
 *
 */
public abstract class AbstractValueBinder implements ValueBinder {
    protected Field mField;
    protected Preference mPreference;

    @Override
    public void setField(Field field) {
        mField = field;
    }

    @Override
    public void setPreference(Preference preference) {
        mPreference = preference;
    }

}
