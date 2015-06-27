package com.jackwink.tweakable.binders;

import java.lang.reflect.Field;

/**
 *
 */
public class StringValueBinder extends AbstractValueBinder<String> {

    public StringValueBinder(Field field) {
        mField = field;
    }

    public Class<String> getType() {
        return String.class;
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
