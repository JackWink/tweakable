package com.jackwink.tweakable.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TwkBoolean {

    // If this string is empty, it will use the class name
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
