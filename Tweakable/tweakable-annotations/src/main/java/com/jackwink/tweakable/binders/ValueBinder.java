package com.jackwink.tweakable.binders;

/**
 * Binds a value type to a given to a field.
 *
 * @param <T> Type of value that will be bound
 */
public interface ValueBinder<T> {

    void bindValue(T value);

    Class<T> getType();
}
