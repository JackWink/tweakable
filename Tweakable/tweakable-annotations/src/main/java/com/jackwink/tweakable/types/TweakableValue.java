package com.jackwink.tweakable.types;

import java.lang.annotation.Annotation;

/**
 *
 */
public interface TweakableValue<T> extends PreferenceEntry {

    Class<T> getType();

    T getValue();

}
