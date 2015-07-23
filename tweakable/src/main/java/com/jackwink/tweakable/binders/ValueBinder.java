package com.jackwink.tweakable.binders;

import android.content.SharedPreferences;

/**
 * Binds a value type to a given to a field.
 *
 * @param <T> Type of value that will be bound
 */
public interface ValueBinder<T> {

    void bindValue(SharedPreferences preferences, String key);

    Class<T> getType();

    T getValue();
}
