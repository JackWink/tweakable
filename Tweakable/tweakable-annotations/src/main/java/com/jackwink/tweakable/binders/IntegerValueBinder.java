package com.jackwink.tweakable.binders;

import java.lang.reflect.Field;

/**
 * Binds an integer value to a static field
 */
public class IntegerValueBinder extends AbstractValueBinder<Integer> {

    public IntegerValueBinder(Field field) {
        mField = field;
    }

    @Override
    public void bindValue(Integer value) {
        try {
            if (mField.getType().equals(int.class)) {
                mField.setInt(null, value);
            } else if (mField.getType().equals(Integer.class)) {
                mField.set(null, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
