package com.jackwink.tweakable.binders;

import android.content.SharedPreferences;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;

import java.lang.reflect.Field;


/**
 *
 */
public class FloatValueBinder extends AbstractValueBinder<Float> {
    public static final Class[] DECLARED_TYPES = {Float.class, float.class };

    public FloatValueBinder(Field field) {
        mField = field;
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

    @Override
    public void bindValue(SharedPreferences preferences, String key) {
        try {
            if (mField.getType().equals(Float.class)) {
                mField.set(null, preferences.getFloat(key, getValue()));
            } else if (mField.getType().equals(float.class)) {
                mField.setFloat(null, preferences.getFloat(key, getValue()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Float getValue() {
        try {
            if (mField.getType().equals(float.class)) {
                return mField.getFloat(null);
            } else if (mField.getType().equals(Float.class)) {
                return (Float) mField.get(null);
            }
            throw new FailedToBuildPreferenceException(
                    "Field " + mField.getName() + " is not a float.");
        } catch (IllegalAccessException e) {
            throw new FailedToBuildPreferenceException(
                    "Field " + mField.getName() + " is protected.", e);
        }
    }
}
