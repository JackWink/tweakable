package com.jackwink.tweakable.types;

import com.jackwink.tweakable.annotations.TwkString;

/**
 *
 */
public class TweakableString  extends AbstractTweakableValue<String> {

    public static final String BUNDLE_OPTIONS_KEY = "options";

    private TwkString mParsedAnnotation;

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String getValue() {
        return mParsedAnnotation.defaultsTo();
    }

    public String[] getOptions() {
        return mParsedAnnotation.options();
    }

    public static TweakableString parse(String className, String fieldName, TwkString annotation) {
        TweakableString returnValue = new TweakableString();

         /* Abstract Tweakable Values */
        returnValue.mKey = className + "." + fieldName;
        returnValue.mTitle = getDefaultString(annotation.title(), fieldName);
        returnValue.mSummary = annotation.summary();
        returnValue.mCategory = getDefaultString(annotation.category(), null);
        returnValue.mScreen = getDefaultString(annotation.screen(), null);

        /* TweakableString */
        returnValue.mParsedAnnotation = annotation;

        return returnValue;
    }

}
