package com.jackwink.tweakable.generators.java;

import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.util.Log;

import com.jackwink.tweakable.controls.ActionPreference;
import com.jackwink.tweakable.controls.NumberPickerPreference;
import com.jackwink.tweakable.controls.SliderPreference;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.types.AbstractTweakableValue;
import com.jackwink.tweakable.types.TweakableBoolean;
import com.jackwink.tweakable.types.TweakableFloat;
import com.jackwink.tweakable.types.TweakableInteger;
import com.jackwink.tweakable.types.TweakableString;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Builds a {@link Preference} from a {@link Bundle}
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PreferenceBuilder<T extends Class> extends BaseBuilder<Preference> {
    private static final String TAG = PreferenceBuilder.class.getSimpleName();

    public static final String BUNDLE_KEYATTR_KEY = AbstractTweakableValue.BUNDLE_KEYATTR_KEY;
    public static final String BUNDLE_TITLE_KEY = AbstractTweakableValue.BUNDLE_TITLE_KEY;
    public static final String BUNDLE_SUMMARY_KEY = AbstractTweakableValue.BUNDLE_SUMMARY_KEY;

    private Context mContext = null;
    private Class<T> mType = null;
    private Object mDefaultValue;

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
     * Sets the default value of the preference.
     *
     * @param defaultValue Default value of this preference
     * @return The updated builder
     */
    public PreferenceBuilder setDefaultValue(Object defaultValue) {
        mDefaultValue = defaultValue;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * Also uses the following for all preferences:
     *
     * <ul>
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
    public PreferenceBuilder<T> setBundle(Map<String, Object> attributeMap) {
        mAttributeMap = attributeMap;
        return this;
    }

    /** {@inheritDoc}*/
    @Override
    public PreferenceBuilder<T> setContext(Context context) {
        mContext = context;
        return this;
    }

    /**
     * Builds a Preference object, can be any of the following types:
     * <ul>
     *     <li>{@link EditTextPreference}</li>
     *     <li>{@link ListPreference}</li>
     *     <li>{@link NumberPickerPreference}</li>
     *     <li>{@link SwitchPreference}</li>
     * </ul>
     * @return An android preference.
     */
    @Override
    public Preference build() {
        if (mType == null) {
            throw new FailedToBuildPreferenceException("Type is null!");
        }

        Preference preference = null;
        if (mType.equals(Boolean.class) || mType.equals(boolean.class)) {
            preference = build(SwitchPreference.class);
            ((SwitchPreference) preference).setChecked((boolean) mDefaultValue);
            ((SwitchPreference) preference).setSummaryOn((String)
                    getOptionalAttribute(TweakableBoolean.BUNDLE_ON_SUMMARY_KEY));
            ((SwitchPreference) preference).setSummaryOff((String)
                    getOptionalAttribute(TweakableBoolean.BUNDLE_OFF_SUMMARY_KEY));
            ((SwitchPreference) preference).setSwitchTextOn((String)
                    getOptionalAttribute(TweakableBoolean.BUNDLE_ON_LABEL_KEY));
            ((SwitchPreference) preference).setSwitchTextOff((String)
                    getOptionalAttribute(TweakableBoolean.BUNDLE_OFF_LABEL_KEY));
        } else if (mType.equals(String.class)) {
            String[] options = (String[]) getRequiredAttribute(TweakableString.BUNDLE_OPTIONS_KEY);
            if (options.length != 0) {
                preference = build(ListPreference.class);
                ((ListPreference) preference).setValue((String) mDefaultValue);
                ((ListPreference) preference).setEntries((String[])
                        getRequiredAttribute(TweakableString.BUNDLE_OPTIONS_KEY));
                ((ListPreference) preference).setEntryValues((String[])
                        getRequiredAttribute(TweakableString.BUNDLE_OPTIONS_KEY));
            } else {
                preference = build(EditTextPreference.class);
                ((EditTextPreference) preference).setText((String) mDefaultValue);
            }
        } else if (mType.equals(Integer.class) || mType.equals(int.class)) {
            preference = build(NumberPickerPreference.class);
            ((NumberPickerPreference) preference).setMaxValue((Integer)
                    getRequiredAttribute(TweakableInteger.BUNDLE_MAX_VALUE_KEY));
            ((NumberPickerPreference) preference).setMinValue((Integer)
                    getRequiredAttribute(TweakableInteger.BUNDLE_MIN_VALUE_KEY));
            ((NumberPickerPreference) preference).setWraps(true);
            ((DialogPreference) preference).setDialogMessage((String)
                    getRequiredAttribute(BUNDLE_SUMMARY_KEY));
        } else if (mType.equals(Float.class) || mType.equals(float.class)) {
            preference = build(SliderPreference.class);
            ((SliderPreference) preference).setMaxValue((Float)
                    getRequiredAttribute(TweakableFloat.BUNDLE_MAX_VALUE_KEY));
            ((SliderPreference) preference).setMinValue((Float)
                    getRequiredAttribute(TweakableFloat.BUNDLE_MIN_VALUE_KEY));
            ((DialogPreference) preference).setDialogMessage((String)
                    getRequiredAttribute(BUNDLE_SUMMARY_KEY));
        } else if (mType.equals(Method.class)) {
            preference = build(ActionPreference.class);
        } else {
            throw new FailedToBuildPreferenceException(
                    "Type: " + mType.getName() + " not supported.");
        }

        return preference;
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
            preference.setDefaultValue(mDefaultValue);
            preference.setPersistent(true);
            if (preference instanceof DialogPreference) {
                ((DialogPreference) preference).setDialogTitle((String)
                        getRequiredAttribute(BUNDLE_TITLE_KEY));
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
        return preference;
    }
}
