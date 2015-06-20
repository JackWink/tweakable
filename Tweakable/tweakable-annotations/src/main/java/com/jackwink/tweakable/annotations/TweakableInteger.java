package com.jackwink.tweakable.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tweak Value
 *
 * Generates a value for a class variable from SharedPreferences, as well as a control
 * in a preferences activity.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TweakableInteger {

    // If this string is empty, it will use the class name
    String category() default "";

    // If string is empty, this will use the field name.
    String name() default "";

    // Default value of the object
    int defaultsTo() default 0;

    // Default value range
    int[] range() default {Integer.MIN_VALUE, Integer.MAX_VALUE};

    String description() default "";
}
