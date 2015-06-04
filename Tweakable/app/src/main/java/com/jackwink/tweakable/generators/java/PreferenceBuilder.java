package com.jackwink.tweakable.generators.java;

import android.content.Context;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;

/**
 * Builds a preference from a Bundle
 */
public class PreferenceBuilder<T extends Class> implements JavaBuilder<Preference> {
    private static final String TAG = PreferenceBuilder.class.getSimpleName();

    public static final String DEFAULT_VALUE_ATTRIBUTE = "defaultsTo";
    public static final String KEY_ATTRIBUTE = "key";
    public static final String TITLE_ATTRIBUTE = "title";
    public static final String SUMMARY_ATTRIBUTE = "summary";
    public static final String ON_LABEL_ATTRIBUTE = "switch_text_on";
    public static final String OFF_LABEL_ATTRIBUTE = "switch_text_off";
    public static final String ON_SUMMARY_ATTRIBUTE = "summary_on";
    public static final String OFF_SUMMARY_ATTRIBUTE = "summary_off";


    private static LinkedHashMap<Class, Class> mTypeToElementMap = new LinkedHashMap<>();

    private Context mContext = null;
    private Class<T> mType = null;
    private LinkedHashMap<String, Object> mAttributeMap = null;

    static {
        mTypeToElementMap.put(boolean.class, SwitchPreference.class);
    }

    public PreferenceBuilder() {
    }

    public PreferenceBuilder setType(Class<T> type) {
        mType = type;
        return this;
    }

    public PreferenceBuilder setAttributeMap(LinkedHashMap<String, Object> attributeMap) {
        mAttributeMap = attributeMap;
        return this;
    }

    public PreferenceBuilder setContext(Context context) {
        mContext = context;
        return this;
    }

    /** {@link JavaBuilder#build()} */
    public Preference build() {
        if (!mTypeToElementMap.containsKey(mType)) {
            throw new IllegalArgumentException(
                    "Type: " + mType.getSimpleName() + " is not supported yet.");
        }

        Class cls = mTypeToElementMap.get(mType);
        Constructor constructor = null;
        Preference preference = null;
        try {
            constructor = cls.getConstructor(Context.class);
            preference = (Preference) constructor.newInstance(mContext);

            /* Required Attributes */
            preference.setKey((String) getAttribute(KEY_ATTRIBUTE));
            preference.setTitle((CharSequence) getAttribute(TITLE_ATTRIBUTE));
            preference.setSummary((String) getAttribute(SUMMARY_ATTRIBUTE));
            preference.setDefaultValue(getAttribute(DEFAULT_VALUE_ATTRIBUTE));

            /* Optional Attributes */

            // Set the initial state to the default value
            if (preference instanceof SwitchPreference) {
                ((SwitchPreference) preference).setChecked(
                        (boolean) getAttribute(DEFAULT_VALUE_ATTRIBUTE));

                ((SwitchPreference) preference).setSummaryOn(
                        (String) getOptionalAttribute(ON_SUMMARY_ATTRIBUTE));
                ((SwitchPreference) preference).setSummaryOff(
                        (String) getOptionalAttribute(OFF_SUMMARY_ATTRIBUTE));

                ((SwitchPreference) preference).setSwitchTextOn(
                        (String) getOptionalAttribute(ON_LABEL_ATTRIBUTE));
                ((SwitchPreference) preference).setSwitchTextOff(
                        (String) getOptionalAttribute(OFF_LABEL_ATTRIBUTE));
            }

            /* Non-User-Configurable Attributes */
            preference.setPersistent(true);
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
        return preference;
    }

    /**
     * Returns the value of a given attribute or throws an exception if it's not found
     *
     * @param attributeName Name of the user-provided attribute to query for
     * @return Value of the provided {@code attributeName}
     */
    private Object getAttribute(String attributeName) throws FailedToBuildPreferenceException {
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
     * @param attributeName Name of the user-provided attribute to query for
     * @return Value of the provided {@code attributeName} or {@code null}
     */
    @Nullable
    private Object getOptionalAttribute(String attributeName) {
        if (!mAttributeMap.containsKey(attributeName)) {
            return null;
        }
        return mAttributeMap.get(attributeName);
    }
}
