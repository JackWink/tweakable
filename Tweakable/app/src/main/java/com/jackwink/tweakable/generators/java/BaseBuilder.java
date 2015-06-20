package com.jackwink.tweakable.generators.java;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;

/**
 *  Contains convenience methods for any {@link JavaBuilder} subclass
 */
public abstract class BaseBuilder<T> implements JavaBuilder<T> {
    private static final String TAG = BaseBuilder.class.getSimpleName();

    /* Builder set attributes */
    protected Bundle mAttributeMap = null;
    protected Context mContext = null;

    /**
     * Returns the value of a given attribute or throws an exception if it's not found
     *
     * @param attributeName Name of the user-provided attribute
     * @return Value of the provided {@code attributeName}
     */
    protected Object getRequiredAttribute(String attributeName) throws FailedToBuildPreferenceException {
        if (!mAttributeMap.containsKey(attributeName)) {
            throw new FailedToBuildPreferenceException(
                    "Missing required attribute: " + attributeName);
        }
        return mAttributeMap.get(attributeName);
    }

    /**
     * Like the {@link #getAttribute(String)} method, except it returns {@code null} instead of
     * throwing an exception.
     *
     * @param attributeName Name of the user-provided attribute
     * @return Value of the provided {@code attributeName} or {@code null}
     */
    @Nullable
    protected Object getOptionalAttribute(String attributeName) {
        if (!mAttributeMap.containsKey(attributeName)) {
            return null;
        }
        return mAttributeMap.get(attributeName);
    }

    public BaseBuilder setBundle(Bundle attributeMap) {
        mAttributeMap = attributeMap;
        return this;
    }

    public BaseBuilder setContext(Context context) {
        mContext = context;
        return this;
    }
}
