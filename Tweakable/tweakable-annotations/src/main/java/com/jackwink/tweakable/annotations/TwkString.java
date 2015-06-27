package com.jackwink.tweakable.annotations;

/**
 *
 */
public @interface TwkString {

    // If this string is empty, preference will be added to the root screen.
    // By providing a name, this will create a subscreen for the preference
    String screen() default "";

    // If this string is empty, preference will be added to the screen root
    String category() default "";

    // If string is empty, this will use the field name.
    String title() default "";

    String summary() default "";

    // Default value of the object
    String defaultsTo();

    String[] options();
}
