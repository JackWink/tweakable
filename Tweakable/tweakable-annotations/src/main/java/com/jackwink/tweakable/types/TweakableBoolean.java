package com.jackwink.tweakable.types;

import android.os.Bundle;

import com.jackwink.tweakable.annotations.TwkBoolean;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 */
public class TweakableBoolean extends AbstractTweakableValue<Boolean> {
    private static final String TAG = TweakableBoolean.class.getSimpleName();

    public static final String BUNDLE_ON_LABEL_KEY = "switch_text_on";
    public static final String BUNDLE_OFF_LABEL_KEY = "switch_text_off";
    public static final String BUNDLE_ON_SUMMARY_KEY = "summary_on";
    public static final String BUNDLE_OFF_SUMMARY_KEY = "summary_off";

    private Boolean mValue;

    private TwkBoolean mParsedAnnotation;


    @Override
    public Class<Boolean> getType() {
        return boolean.class;
    }

    @Override
    public Boolean getValue() {
        return mParsedAnnotation.defaultsTo();
    }

    @Override
    public Bundle toBundle() {
        Bundle base = super.toBundle();
        base.putBoolean(AbstractTweakableValue.BUNDLE_DEFAULT_VALUE_KEY,
                mParsedAnnotation.defaultsTo());

        if (!mParsedAnnotation.offSummary().isEmpty()) {
            base.putString(BUNDLE_OFF_SUMMARY_KEY, mParsedAnnotation.offSummary());
        }

        if (!mParsedAnnotation.onSummary().isEmpty()) {
            base.putString(BUNDLE_ON_SUMMARY_KEY, mParsedAnnotation.onSummary());
        }

        // TODO: these don't seem to work in Android 5.x, need to test 4.x
        if (!mParsedAnnotation.onLabel().isEmpty()) {
            base.putString(BUNDLE_ON_LABEL_KEY, mParsedAnnotation.onLabel());
        }

        if (!mParsedAnnotation.offLabel().isEmpty()) {
            base.putString(BUNDLE_OFF_LABEL_KEY, mParsedAnnotation.offLabel());
        }
        return base;
    }

    public static TweakableBoolean parse(Class cls, Field field) {
        if (!field.isAnnotationPresent(TwkBoolean.class) ||
                !Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Field is not annotated with TwkBoolean annotation.");
        }
        TwkBoolean annotation = field.getAnnotation(TwkBoolean.class);

        TweakableBoolean returnValue = new TweakableBoolean();

        /* Abstract Tweakable Values */
        returnValue.mKey = cls.getName() + "-" + field.getName();
        returnValue.mTitle = getDefaultString(annotation.title(), field.getName());
        returnValue.mSummary = annotation.summary();
        returnValue.mCategory = getDefaultString(annotation.category(), null);
        returnValue.mScreen = getDefaultString(annotation.screen(), null);

        /* TweakableBoolean */
        returnValue.mParsedAnnotation = annotation;
        return returnValue;
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
