package com.jackwink.tweakable.types;

import com.jackwink.tweakable.annotations.TwkInteger;

/**
 *
 */
public class TweakableInteger extends AbstractTweakableValue<Integer> {

    public static final String BUNDLE_MIN_VALUE_KEY = "min_value";
    public static final String BUNDLE_MAX_VALUE_KEY = "max_value";

    private TwkInteger mParsedAnnotation;

    public Integer getMaxValue() {
        return mParsedAnnotation.maxValue();
    }

    public Integer getMinValue() {
        return mParsedAnnotation.minValue();
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    public static TweakableInteger parse(String className, String fieldName, TwkInteger annotation) {
        TweakableInteger returnValue = new TweakableInteger();

        /* Abstract Tweakable Values */
        returnValue.mKey = className + "." + fieldName;
        returnValue.mTitle = getDefaultString(annotation.title(), fieldName);
        returnValue.mSummary = annotation.summary();
        returnValue.mCategory = getDefaultString(annotation.category(), null);
        returnValue.mScreen = getDefaultString(annotation.screen(), null);

        /* TweakableInteger */
        returnValue.mParsedAnnotation = annotation;
        return returnValue;
    }
}
