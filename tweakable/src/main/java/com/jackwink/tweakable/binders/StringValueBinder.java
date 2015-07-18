package com.jackwink.tweakable.binders;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;

import java.lang.reflect.Field;

/**
 * Binds a String value to a given {@link Field}.
 */
public class StringValueBinder extends AbstractValueBinder<String> {

    public StringValueBinder(Field field) {
        mField = field;
    }

    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String getValue() {
        try {
            if (mField.getType().equals(String.class)) {
                return (String) mField.get(null);
            }
            throw new FailedToBuildPreferenceException(
                    "Field " + mField.getName() +  " is not a string.");
        } catch (IllegalAccessException e) {
            throw new FailedToBuildPreferenceException(
                    "Field " + mField.getName() + " is protected.", e);
        }
    }

    @Override
    public void bindValue(String value) {
        try {
            if (mField.getType().equals(String.class)) {
                mField.set(null, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
