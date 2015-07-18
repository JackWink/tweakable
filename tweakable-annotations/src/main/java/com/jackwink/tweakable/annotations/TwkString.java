package com.jackwink.tweakable.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Declares a user configurable String field.
 *
 *  If {@link #options()} is an empty array, the user will be shown an {@code EditTextPreference},
 *  otherwise, the user will see a {@code ListPreference} with the provided options.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface TwkString {

    // If this string is empty, preference will be added to the root screen.
    // By providing a name, this will create a subscreen for the preference
    String screen() default "";

    // If this string is empty, preference will be added to the screen root
    String category() default "";

    // If string is empty, this will use the field name.
    String title() default "";

    String summary() default "";

    // If left to an empty array, this will be an editable text field.
    String[] options() default {};
}
