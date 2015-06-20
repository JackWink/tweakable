package com.jackwink.tweakable.types;

/**
 *
 */
public interface TweakableValue<T> extends PreferenceEntry {

    Class<T> getType();

    T getValue();

}
