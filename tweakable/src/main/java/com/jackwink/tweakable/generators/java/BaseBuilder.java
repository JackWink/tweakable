package com.jackwink.tweakable.generators.java;

import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.Log;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.types.AbstractTweakableValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 *  Contains convenience methods for any {@link JavaBuilder} subclass
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class BaseBuilder<T> implements JavaBuilder<T> {
    private static final String TAG = BaseBuilder.class.getSimpleName();

    public static final String BUNDLE_KEYATTR_KEY = AbstractTweakableValue.BUNDLE_KEYATTR_KEY;
    public static final String BUNDLE_TITLE_KEY = AbstractTweakableValue.BUNDLE_TITLE_KEY;
    public static final String BUNDLE_SUMMARY_KEY = AbstractTweakableValue.BUNDLE_SUMMARY_KEY;

    /* Builder set attributes */
    protected Map<String, Object> mAttributeMap = null;
    protected Context mContext = null;
    protected Object mDefaultValue = null;

    /**
     * Returns the value of a given attribute or throws an exception if it's not found
     *
     * @param attributeName Name of the user-provided attribute
     * @return Value of the provided {@code attributeName}
     * @throws FailedToBuildPreferenceException if bundle is missing an attribute
     */
    protected Object getRequiredAttribute(String attributeName) {
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
    public BaseBuilder<T> setBundle(Map<String, Object> attributeMap) {
        mAttributeMap = attributeMap;
        return this;
    }

    /**
     * Sets a context used to generate the preference object
     * @param context
     * @return The updated builder
     */
    public BaseBuilder<T> setContext(Context context) {
        mContext = context;
        return this;
    }

    /**
     * Sets the default value of the preference.
     *
     * @param defaultValue Default value of this preference
     * @return The updated builder
     */
    public BaseBuilder<T> setDefaultValue(Object defaultValue) {
        mDefaultValue = defaultValue;
        return this;
    }

    @Override
    public void reset() {
        mDefaultValue = null;
        mContext = null;
        mAttributeMap = null;
    }

    protected <V extends Preference> V build(Class<V> cls, boolean persistent) {
        Constructor constructor = null;
        Preference preference = null;
        try {
            constructor = cls.getConstructor(Context.class);
            preference = (Preference) constructor.newInstance(mContext);

            /* Required Attributes */
            String title = (String) getRequiredAttribute(BUNDLE_TITLE_KEY);
            preference.setKey((String) getRequiredAttribute(BUNDLE_KEYATTR_KEY));
            preference.setTitle(title);
            preference.setSummary((String) getRequiredAttribute(BUNDLE_SUMMARY_KEY));
            preference.setDefaultValue(mDefaultValue);
            preference.setPersistent(persistent);
            if (preference instanceof DialogPreference) {
                ((DialogPreference) preference).setDialogTitle(title);
            }
        } catch (InstantiationException error) {
            Log.e(TAG, "InstantiationException!");
            throw new FailedToBuildPreferenceException(
                    "Could not find default constructor for " + cls.getSimpleName(), error);
        } catch (IllegalAccessException error) {
            Log.e(TAG, "Illegal Access!");
            throw new FailedToBuildPreferenceException(
                    "Could not access constructor for " + cls.getSimpleName(), error);
        } catch (NoSuchMethodException error) {
            Log.e(TAG, "No preference constructor with single argument: 'Context'!");
            throw new FailedToBuildPreferenceException(
                    "No 'Context'-only constructor for " + cls.getSimpleName(), error);
        } catch (InvocationTargetException error) {
            Log.e(TAG, "Invalid target?");
            throw new FailedToBuildPreferenceException(
                    "Invalid constructor target for: " + cls.getSimpleName(), error);
        }
        return (V) preference;
    }
}
