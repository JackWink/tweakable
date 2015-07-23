package com.jackwink.tweakable.binders;

import android.content.SharedPreferences;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;

import java.lang.reflect.Field;

/**
 * Binds an integer value to a static field
 */
public class IntegerValueBinder extends AbstractValueBinder<Integer> {
    public static final Class[] DECLARED_TYPES = {Integer.class, int.class };

    public IntegerValueBinder(Field field) {
        mField = field;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public Integer getValue() {
        try {
            if (mField.getType().equals(int.class)) {
                return mField.getInt(null);
            } else if (mField.getType().equals(Integer.class)) {
                return (Integer) mField.get(null);
            }
            throw new FailedToBuildPreferenceException(
                    "Field " + mField.getName() + " is not an integer.");
        } catch (IllegalAccessException e) {
            throw new FailedToBuildPreferenceException(
                    "Field " + mField.getName() + " is protected.", e);
        }
    }

    @Override
    public void bindValue(SharedPreferences preferences, String key) {
        try {
            if (mField.getType().equals(int.class)) {
                mField.setInt(null, preferences.getInt(key, getValue()));
            } else if (mField.getType().equals(Integer.class)) {
                mField.set(null, preferences.getInt(key, getValue()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
