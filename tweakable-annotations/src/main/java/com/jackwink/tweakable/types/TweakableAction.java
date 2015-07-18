package com.jackwink.tweakable.types;

import com.jackwink.tweakable.annotations.TwkAction;

import java.lang.reflect.Method;

/**
 *
 */
public class TweakableAction extends AbstractTweakableValue<Method> {

    private TwkAction mParsedAnnotation;

    @Override
    public Class<Method> getType() {
        return Method.class;
    }

    public static TweakableAction parse(String className, String methodName, TwkAction annotation) {
        TweakableAction returnValue = new TweakableAction();

        /* Abstract Tweakable Values */
        returnValue.mKey = className + "." + methodName;
        returnValue.mTitle = getDefaultString(annotation.title(), methodName);
        returnValue.mSummary = annotation.summary();
        returnValue.mCategory = getDefaultString(annotation.category(), null);
        returnValue.mScreen = getDefaultString(annotation.screen(), null);

        /* TweakableBoolean */
        returnValue.mParsedAnnotation = annotation;
        return returnValue;
    }
}
