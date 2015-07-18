package com.jackwink.tweakable.types;

import com.jackwink.tweakable.annotations.TwkBoolean;

/**
 *
 */
public class TweakableBoolean extends AbstractTweakableValue<Boolean> {
    private static final String TAG = TweakableBoolean.class.getSimpleName();

    public static final String BUNDLE_ON_LABEL_KEY = "switch_text_on";
    public static final String BUNDLE_OFF_LABEL_KEY = "switch_text_off";
    public static final String BUNDLE_ON_SUMMARY_KEY = "summary_on";
    public static final String BUNDLE_OFF_SUMMARY_KEY = "summary_off";

    private TwkBoolean mParsedAnnotation;


    public String getOnLabel() {
        return mParsedAnnotation.onLabel();
    }

    public String getOffLabel() {
        return mParsedAnnotation.offLabel();
    }

    public String getOnSummary() {
        return mParsedAnnotation.onSummary();
    }

    public String getOffSummary() {
        return mParsedAnnotation.offSummary();
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    public static TweakableBoolean parse(String className, String fieldName, TwkBoolean annotation) {
        TweakableBoolean returnValue = new TweakableBoolean();

        /* Abstract Tweakable Values */
        returnValue.mKey = className + "." + fieldName;
        returnValue.mTitle = getDefaultString(annotation.title(), fieldName);
        returnValue.mSummary = annotation.summary();
        returnValue.mCategory = getDefaultString(annotation.category(), null);
        returnValue.mScreen = getDefaultString(annotation.screen(), null);

        /* TweakableBoolean */
        returnValue.mParsedAnnotation = annotation;
        return returnValue;
    }

}
