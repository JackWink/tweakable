package com.jackwink.tweakable.builders;

import android.preference.Preference;

/**
 *  Builds a java object
 */
public interface PreferenceBuilder<T extends Preference> {

    /**
     * Lists the registered classes this builder can handle.  For example, a builder that
     * constructs preferences for a {@link com.jackwink.tweakable.annotations.TwkBoolean}
     * would declare {@code Boolean} and {@code boolean}
     *
     * @return Array of types handled
     */
    Class[] getHandledTypes();

    /**
     * Builds the Java object
     *
     * @return Object of type {@code T}
     */
    T build();

    /**
     * Clears the builder
     */
    void reset();
}
