package com.jackwink.tweakable.binders;

import java.lang.reflect.Field;

public interface ValueBinder<T> {

    void setField(Field field);

    void bindValue(T value);

    Class<T> getType();
}
