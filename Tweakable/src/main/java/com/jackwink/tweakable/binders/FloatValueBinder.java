package com.jackwink.tweakable.binders;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;

import java.lang.reflect.Field;

/**
 *
 */
public class FloatValueBinder extends AbstractValueBinder<Float> {

    public FloatValueBinder(Field field) {
        mField = field;
    }

    @Override
    public void bindValue(Float value) {
        try {
            if (mField.getType().equals(Float.class)) {
                mField.set(null, value);
            } else if (mField.getType().equals(float.class)) {
                mField.setFloat(null, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
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
