package com.jackwink.tweakable.types;

import com.jackwink.tweakable.annotations.TwkFloat;

/**
 *
 */
public class TweakableFloat extends AbstractTweakableValue<Float> {

    public static final String BUNDLE_MIN_VALUE_KEY = "min_value";
    public static final String BUNDLE_MAX_VALUE_KEY = "max_value";

    private TwkFloat mParsedAnnotation;

    public Float getMaxValue() {
        return mParsedAnnotation.maxValue();
    }

    public Float getMinValue() {
        return mParsedAnnotation.minValue();
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

    public static TweakableFloat parse(String className, String fieldName, TwkFloat annotation) {
        TweakableFloat returnValue = new TweakableFloat();

        /* Abstract Tweakable Values */
        returnValue.mKey = className + "." + fieldName;
        returnValue.mTitle = getDefaultString(annotation.title(), fieldName);
        returnValue.mSummary = annotation.summary();
        returnValue.mCategory = getDefaultString(annotation.category(), null);
        returnValue.mScreen = getDefaultString(annotation.screen(), null);

        /* TweakableFloat */
        returnValue.mParsedAnnotation = annotation;
        return returnValue;
    }
}
