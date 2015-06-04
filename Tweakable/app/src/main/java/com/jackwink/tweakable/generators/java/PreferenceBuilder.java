package com.jackwink.tweakable.generators.java;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.util.Log;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds a preference from a Bundle
 */
public class PreferenceBuilder<T extends Class> implements JavaBuilder<Preference> {
    private static final String TAG = PreferenceBuilder.class.getSimpleName();

    public static final String DEFAULT_VALUE_ATTRIBUTE = "defaultsTo";

    private static LinkedHashMap<Class, Class> mTypeToElementMap = new LinkedHashMap<>();

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

    /** {@link JavaBuilder#build()} */
    public Preference build() {
        if (!mTypeToElementMap.containsKey(mType)) {
            throw new IllegalArgumentException(
                    "Type: " + mType.getSimpleName() + " is not supported yet.");
        }

        Class cls = mTypeToElementMap.get(mType);
        Preference preference = null;
        try {
            preference = (Preference) cls.newInstance();
            if (mAttributeMap.containsKey(DEFAULT_VALUE_ATTRIBUTE)) {
                preference.setDefaultValue(mAttributeMap.get(DEFAULT_VALUE_ATTRIBUTE));
            }
            

        } catch (InstantiationException error) {
            Log.e(TAG, "InstantiationException!");
            throw new FailedToBuildPreferenceException(
                    "Could not find default constructor for " + cls.getSimpleName(), error);
        } catch (IllegalAccessException error) {
            Log.e(TAG, "Illegal Access!");
            throw new FailedToBuildPreferenceException(
                    "Could not access constructor for " + cls.getSimpleName(), error);
        }
        return preference;
    }
}
