package com.jackwink.tweakable.types;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jackwink.tweakable.annotations.TweakableInteger;
import com.jackwink.tweakable.annotations.TwkBoolean;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 */
public class TweakableBoolean extends AbstractTweakableValue<Boolean> {
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

    @Nullable
    @Override
    public Bundle toBundle() {
        Bundle base = super.toBundle();
        base.putBoolean(AbstractTweakableValue.BUNDLE_DEFAULT_VALUE_KEY, mParsedAnnotation.defaultsTo());
        if (!mParsedAnnotation.offSummary().isEmpty()) {
            base.putString(BUNDLE_OFF_SUMMARY_KEY, mParsedAnnotation.offSummary());
        }

        if (!mParsedAnnotation.onSummary().isEmpty()) {
            base.putString(BUNDLE_ON_SUMMARY_KEY, mParsedAnnotation.onSummary());
        }

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
        returnValue.mKey = cls.getName() + "." + field.getName();
        returnValue.mTitle = !annotation.title().isEmpty() ? annotation.title() : field.getName();
        returnValue.mSummary = annotation.summary();
        returnValue.mParsedAnnotation = annotation;
        return returnValue;
    }
}
