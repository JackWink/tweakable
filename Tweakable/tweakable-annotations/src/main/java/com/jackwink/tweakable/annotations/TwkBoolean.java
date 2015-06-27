package com.jackwink.tweakable.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a user configurable Boolean or boolean field.
 *
 * Note:  On Android 5.x, and possibly 4.x, the {@link #onLabel()} and {@link #offLabel()}
 * do not seem to work.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface TwkBoolean {

    // If this string is empty, preference will be added to the root screen.
    // By providing a name, this will create a subscreen for the preference
    String screen() default "";

    // If this string is empty, preference will be added to the screen root
    String category() default "";

    // If string is empty, this will use the field name.
    String title() default "";

    // Default value of the object
    boolean defaultsTo() default false;

    String summary() default "";

    String onSummary() default "";
    String onLabel() default "";

    String offSummary() default "";
    String offLabel() default "";
}
