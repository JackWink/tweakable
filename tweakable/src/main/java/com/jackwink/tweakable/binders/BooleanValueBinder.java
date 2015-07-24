package com.jackwink.tweakable.binders;


import android.content.SharedPreferences;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;

import java.lang.reflect.Field;

/**
 * Binds a boolean value to a given field
 */
public class BooleanValueBinder extends AbstractValueBinder<Boolean> {

    public BooleanValueBinder(Field field) {
        mField = field;
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public Boolean getValue() {
        try {
            if (mField.getType().equals(boolean.class)) {
                return mField.getBoolean(null);
            } else if (mField.getType().equals(Boolean.class)) {
                return (Boolean) mField.get(null);
            }
            throw new FailedToBuildPreferenceException(
                    "Field " + mField.getName() + " is not a boolean.");
        } catch (IllegalAccessException e) {
            throw new FailedToBuildPreferenceException(
                    "Field " + mField.getName() + " is protected.", e);
        }
    }

    @Override
    public void bindValue(SharedPreferences preferences, String key) {
        try {
            if (mField.getType().equals(boolean.class)) {
                mField.setBoolean(null, preferences.getBoolean(key, getValue()));
            } else if (mField.getType().equals(Boolean.class)) {
                mField.set(null, preferences.getBoolean(key, getValue()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
