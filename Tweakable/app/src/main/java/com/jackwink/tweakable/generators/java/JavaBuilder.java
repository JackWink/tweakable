package com.jackwink.tweakable.generators.java;

/**
 *  Builds a java object
 *
 *  Note: Quicker to figure this interface out, but bigger impact on runtime :/
 */
public interface JavaBuilder<T> {
    T build();
}
