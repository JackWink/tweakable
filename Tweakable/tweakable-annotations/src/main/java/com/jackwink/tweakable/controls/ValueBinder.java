package com.jackwink.tweakable.controls;

import android.preference.Preference;

import java.lang.reflect.Field;

public interface ValueBinder {
    void setPreference(Preference preference);

    void setField(Field field);

    void bindValue();
}
