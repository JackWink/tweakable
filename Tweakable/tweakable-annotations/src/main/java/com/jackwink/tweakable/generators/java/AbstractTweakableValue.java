package com.jackwink.tweakable.generators.java;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.util.Log;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.types.TweakableBoolean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;

/**
 * Builds a preference from a Bundle
 */
public class AbstractTweakableValue<T extends Class> extends BaseBuilder<Preference> {
    private static final String TAG = AbstractTweakableValue.class.getSimpleName();

    public static final String BUNDLE_KEYATTR_KEY = com.jackwink.tweakable.types.AbstractTweakableValue.BUNDLE_KEYATTR_KEY;
    public static final String BUNDLE_TITLE_KEY = com.jackwink.tweakable.types.AbstractTweakableValue.BUNDLE_TITLE_KEY;
    public static final String BUNDLE_SUMMARY_KEY = com.jackwink.tweakable.types.AbstractTweakableValue.BUNDLE_SUMMARY_KEY;
    public static final String BUNDLE_TYPEINFO_KEY = com.jackwink.tweakable.types.AbstractTweakableValue.BUNDLE_TYPEINFO_KEY;
    public static final String BUNDLE_DEFAULT_VALUE_KEY =
            com.jackwink.tweakable.types.AbstractTweakableValue.BUNDLE_DEFAULT_VALUE_KEY;

    private static LinkedHashMap<Class, Class> mTypeToElementMap = new LinkedHashMap<>();

    private Context mContext = null;
    private Class<T> mType = null;

    static {
        mTypeToElementMap.put(boolean.class, SwitchPreference.class);
    }

    public AbstractTweakableValue() {
    }

    public AbstractTweakableValue setType(Class<T> type) {
        mType = type;
        return this;
    }

    @Override
    public AbstractTweakableValue setBundle(Bundle attributeMap) {
        mAttributeMap = attributeMap;
        return this;
    }

    @Override
    public AbstractTweakableValue setContext(Context context) {
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
            preference.setKey((String) getRequiredAttribute(BUNDLE_KEYATTR_KEY));
            preference.setTitle((CharSequence) getRequiredAttribute(BUNDLE_TITLE_KEY));
            preference.setSummary((String) getRequiredAttribute(BUNDLE_SUMMARY_KEY));
            preference.setDefaultValue(getRequiredAttribute(BUNDLE_DEFAULT_VALUE_KEY));

            /* Optional Attributes */

            // Set the initial state to the default value
            if (preference instanceof SwitchPreference) {
                ((SwitchPreference) preference).setChecked(
                        (boolean) getRequiredAttribute(BUNDLE_DEFAULT_VALUE_KEY));

                ((SwitchPreference) preference).setSummaryOn(
                        (String) getOptionalAttribute(TweakableBoolean.BUNDLE_ON_SUMMARY_KEY));
                ((SwitchPreference) preference).setSummaryOff(
                        (String) getOptionalAttribute(TweakableBoolean.BUNDLE_OFF_SUMMARY_KEY));

                ((SwitchPreference) preference).setSwitchTextOn(
                        (String) getOptionalAttribute(TweakableBoolean.BUNDLE_ON_LABEL_KEY));

                ((SwitchPreference) preference).setSwitchTextOff(
                        (String) getOptionalAttribute(TweakableBoolean.BUNDLE_OFF_LABEL_KEY));
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

}
