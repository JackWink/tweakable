package com.jackwink.tweakable.binders;


import java.lang.reflect.Field;

/**
 * Binds a boolean value from shared preferences to a static field
 */
public class BooleanValueBinder extends AbstractValueBinder<Boolean> {

    public BooleanValueBinder(Field field) {
        mField = field;
    }

    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public void bindValue(Boolean value) {
        try {
            if (mField.getType().equals(boolean.class)) {
                mField.setBoolean(null, value);
            } else if (mField.getType().equals(Boolean.class)) {
                mField.set(null, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
