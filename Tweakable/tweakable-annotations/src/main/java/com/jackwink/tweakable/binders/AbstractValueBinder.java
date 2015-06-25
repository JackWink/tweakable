package com.jackwink.tweakable.binders;

import java.lang.reflect.Field;

/**
 *
 */
public abstract class AbstractValueBinder<T> implements ValueBinder<T> {
    protected Field mField;

    @Override
    public void setField(Field field) {
        mField = field;
    }

}
