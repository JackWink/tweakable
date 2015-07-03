package com.jackwink.tweakable.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface TwkFloat {
    // If this string is empty, preference will be added to the root screen.
    // By providing a name, this will create a subscreen for the preference
    String screen() default "";

    // If this string is empty, preference will be added to the screen root
    String category() default "";

    // If string is empty, this will use the field name.
    String title() default "";

    // Description of the value
    String summary() default "";

    float maxValue() default Float.MAX_VALUE;

    float minValue() default Float.MIN_VALUE;
}
