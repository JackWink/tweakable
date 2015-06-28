package com.jackwink.tweakable.generators.java;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.util.Log;

import com.jackwink.tweakable.controls.NumberPickerPreference;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.types.AbstractTweakableValue;
import com.jackwink.tweakable.types.TweakableBoolean;
import com.jackwink.tweakable.types.TweakableInteger;
import com.jackwink.tweakable.types.TweakableString;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;

/**
 * Builds a {@link Preference} from a {@link Bundle}
 */
public class PreferenceBuilder<T extends Class> extends BaseBuilder<Preference> {
    private static final String TAG = PreferenceBuilder.class.getSimpleName();

    public static final String BUNDLE_KEYATTR_KEY = AbstractTweakableValue.BUNDLE_KEYATTR_KEY;
    public static final String BUNDLE_TITLE_KEY = AbstractTweakableValue.BUNDLE_TITLE_KEY;
    public static final String BUNDLE_SUMMARY_KEY = AbstractTweakableValue.BUNDLE_SUMMARY_KEY;
    public static final String BUNDLE_TYPEINFO_KEY = AbstractTweakableValue.BUNDLE_TYPEINFO_KEY;
    public static final String BUNDLE_DEFAULT_VALUE_KEY =
            AbstractTweakableValue.BUNDLE_DEFAULT_VALUE_KEY;

    private static LinkedHashSet<Class> mSupportedTypes = new LinkedHashSet<>(2);

    private Context mContext = null;
    private Class<T> mType = null;

    static {
        mSupportedTypes.add(boolean.class);
        mSupportedTypes.add(String.class);
        mSupportedTypes.add(Integer.class);
    }

    public PreferenceBuilder() {
    }

    /**
     * Sets the annotated field type to decide which preference to build.
     * @param type Type of the annotated field.
     * @return The updated builder
     */
    public PreferenceBuilder setType(Class<T> type) {
        mType = type;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * Also uses the following for all preferences:
     *
     * <ul>
     *     <li>{@link AbstractTweakableValue#BUNDLE_DEFAULT_VALUE_KEY}</li>
     *     <li>{@link AbstractTweakableValue#BUNDLE_SUMMARY_KEY}</li>
     * </ul>
     *
     * And the following for boolean preferences:
     *
     * <ul>
     *     <li>{@link TweakableBoolean#BUNDLE_ON_LABEL_KEY}</li>
     *     <li>{@link TweakableBoolean#BUNDLE_OFF_LABEL_KEY}</li>
     *     <li>{@link TweakableBoolean#BUNDLE_ON_SUMMARY_KEY}</li>
     *     <li>{@link TweakableBoolean#BUNDLE_OFF_SUMMARY_KEY}</li>
     * </ul>
     *
     * And the following for Strings:
     * <ul>
     *     <li>{@link TweakableString#BUNDLE_OPTIONS_KEY}</li>
     * </ul>
     */
    @Override
    public PreferenceBuilder setBundle(Bundle attributeMap) {
        mAttributeMap = attributeMap;
        return this;
    }

    /** {@inheritDoc}*/
    @Override
    public PreferenceBuilder setContext(Context context) {
        mContext = context;
        return this;
    }

    /**
     * Builds a Preference object, can be any of the following types:
     * <ul>
     *     <li>{@link EditTextPreference}</li>
     *     <li>{@link ListPreference}</li>
     *     <li>{@link SwitchPreference}</li>
     * </ul>
     * @return An android preference.
     */
    @Override
    public Preference build() {
        if (!mSupportedTypes.contains(mType)) {
            throw new IllegalArgumentException(
                    "Type: " + mType.getSimpleName() + " is not supported yet.");
        }

        if (mType.equals(boolean.class)) {
            return build(SwitchPreference.class);
        } else if (mType.equals(String.class) &&
                ((String[]) getRequiredAttribute(TweakableString.BUNDLE_OPTIONS_KEY)).length != 0) {
            return build(ListPreference.class);
        } else if (mType.equals(String.class)) {
            return build(EditTextPreference.class);
        } else if (mType.equals(Integer.class)) {
            return build(NumberPickerPreference.class);
        }

        Log.i(TAG, "What?: " + mType.getName());
        return null;
    }

    /**
     * Internal builder to be used to build specific preference types.
     *
     * @param cls Type of preference to be built
     * @return fully configured {@link Preference} object
     */
    private Preference build(Class cls) {
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
            if (cls.equals(SwitchPreference.class)) {
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
            } else if (cls.equals(ListPreference.class)) {
                ListPreference lp = (ListPreference) preference;
                lp.setValue((String) getRequiredAttribute(BUNDLE_DEFAULT_VALUE_KEY));
                lp.setEntries((String[]) getRequiredAttribute(TweakableString.BUNDLE_OPTIONS_KEY));
                lp.setEntryValues((String[])
                        getRequiredAttribute(TweakableString.BUNDLE_OPTIONS_KEY));
            } else if (cls.equals(NumberPickerPreference.class)) {
                NumberPickerPreference pref = (NumberPickerPreference) preference;
                pref.setMaxValue((Integer)
                        getRequiredAttribute(TweakableInteger.BUNDLE_MAX_VALUE_KEY));
                pref.setMinValue((Integer)
                        getRequiredAttribute(TweakableInteger.BUNDLE_MIN_VALUE_KEY));
                pref.setWraps(true);
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
