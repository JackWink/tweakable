package com.jackwink.tweakable.generators.java;

import android.content.Context;
import android.os.Bundle;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;

import java.util.Map;

/**
 *  Contains convenience methods for any {@link JavaBuilder} subclass
 */
public abstract class BaseBuilder<T> implements JavaBuilder<T> {
    private static final String TAG = BaseBuilder.class.getSimpleName();

    /* Builder set attributes */
    protected Map<String, Object> mAttributeMap = null;
    protected Context mContext = null;

    /**
     * Returns the value of a given attribute or throws an exception if it's not found
     *
     * @param attributeName Name of the user-provided attribute
     * @return Value of the provided {@code attributeName}
     * @throws FailedToBuildPreferenceException if bundle is missing an attribute
     */
    protected Object getRequiredAttribute(String attributeName) throws FailedToBuildPreferenceException {
        if (!mAttributeMap.containsKey(attributeName)) {
            throw new FailedToBuildPreferenceException(
                    "Missing required attribute: " + attributeName);
        }
        return mAttributeMap.get(attributeName);
    }

    /**
     * Like the {@link #getRequiredAttribute(String)} method, except it returns {@code null} instead
     * of throwing an exception.
     *
     * @param attributeName Name of the user-provided attribute
     * @return Value of the provided {@code attributeName} or {@code null}
     */
    protected Object getOptionalAttribute(String attributeName) {
        if (!mAttributeMap.containsKey(attributeName)) {
            return null;
        }
        return mAttributeMap.get(attributeName);
    }

    /**
     * Sets the internal {@link Bundle} used to generate the preference object.
     * <p>The following values are required for all preferences:</p>
     * <ul>
     *     <li>
     *         {@link com.jackwink.tweakable.types.AbstractTweakableValue#BUNDLE_KEYATTR_KEY}
     *     </li>
     *     <li>
     *         {@link com.jackwink.tweakable.types.AbstractTweakableValue#BUNDLE_TITLE_KEY}
     *     </li>
     * </ul>
     * @param attributeMap Bundle containing values used to construct the preference
     * @return The updated builder
     */
    public BaseBuilder setBundle(Map<String, Object> attributeMap) {
        mAttributeMap = attributeMap;
        return this;
    }

    /**
     * Sets a context used to generate the preference object
     * @param context
     * @return The updated builder
     */
    public BaseBuilder setContext(Context context) {
        mContext = context;
        return this;
    }
}
