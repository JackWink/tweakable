package com.jackwink.tweakable;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.jackwink.tweakable.binders.AbstractValueBinder;
import com.jackwink.tweakable.binders.BooleanValueBinder;
import com.jackwink.tweakable.binders.IntegerValueBinder;
import com.jackwink.tweakable.binders.StringValueBinder;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.types.AbstractTweakableValue;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

/**
 * Tweakable values and Feature Flags for Android.
 *
 * <h2>Usage Instructions</h2>
 *
 * <p>Call {@link Tweakable#init(Context)} to inject the default values
 * (or saved values from Preferences) on the start of your application or main activity.</p>
 *
 * <p>Later, show an instance of {@link TweaksFragment} to the user.  Upon updating the values
 * in the fragment, the updated values will be saved to {@link SharedPreferences} and the annotated
 * fields will be updated immediately.</p>
 */
public class Tweakable {
    private static final String TAG = Tweakable.class.getSimpleName();

    private static String mSharedPreferencesName;
    private static SharedPreferences mSharedPreferences;

    private static LinkedHashMap<String, AbstractValueBinder> mValueBinders =
            new LinkedHashMap<>();

    /**
     * Binds the default values (or saved values) to all annotated fields.
     *
     * @param context Application or activity context used to retrieve the shared preferences file.
     */
    public static void init(Context context) {
        mSharedPreferencesName = context.getPackageName() + "_preferences";
        mSharedPreferences = context.getSharedPreferences(mSharedPreferencesName,
                Context.MODE_PRIVATE);
        for (Bundle bundle: getPreferences().getDeclaredPreferences()) {
            String preferenceKey = bundle.getString(AbstractTweakableValue.BUNDLE_KEYATTR_KEY);

            /* Set default / saved values init */
            String fieldName = preferenceKey.substring(preferenceKey.lastIndexOf('.') + 1);
            String clsName = preferenceKey.substring(0, preferenceKey.lastIndexOf('.'));
            Log.i(TAG, "Updating '" + clsName + "' field: " + fieldName);

            AbstractValueBinder binder = null;
            Field field = null;
            try {
                field = Class.forName(clsName).getDeclaredField(fieldName);

                if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                    binder = new BooleanValueBinder(field);
                    ((BooleanValueBinder) binder).bindValue(mSharedPreferences.getBoolean(
                            preferenceKey, (boolean) binder.getValue()));
                } else if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                    binder = new IntegerValueBinder(field);
                    ((IntegerValueBinder) binder).bindValue(mSharedPreferences.getInt(preferenceKey,
                            (int) binder.getValue()));
                } else if (field.getType().equals(String.class)) {
                    binder = new StringValueBinder(field);
                    ((StringValueBinder) binder).bindValue(mSharedPreferences.getString(
                            preferenceKey, (String) binder.getValue()));
                } else {
                    throw new FailedToBuildPreferenceException(
                            "No value binder exists for: " + field.getType().toString());
                }
                Log.i(TAG, "Value: " + binder.getValue());
                mValueBinders.put(preferenceKey, binder);
            } catch (Exception error) {
                throw new FailedToBuildPreferenceException("Failed to build value binders.", error);
            }
        }
    }

    protected static void bindValue(String key, Object value) {
        if (mValueBinders.containsKey(key)) {
            //noinspection unchecked
            mValueBinders.get(key).bindValue(value);
        }
    }

    protected static Class getType(String key) {
        return mValueBinders.get(key).getType();
    }

    protected static Object getValue(String key) {
        return mValueBinders.get(key).getValue();
    }

    /**
     * <p>Returns an instance of the generated preferences class, or throws an exception if it can't
     * be found. For internal use only!</p>
     * @return The generated {@link TweaksFragment.PreferenceAnnotationProcessor}
     */
    protected static TweaksFragment.PreferenceAnnotationProcessor getPreferences() {
        try {
            return (TweaksFragment.PreferenceAnnotationProcessor)
                    Class.forName("com.jackwink.tweakable.GeneratedPreferences").newInstance();
        } catch (Exception e) {
            throw new FailedToBuildPreferenceException("Could not find generated preferences.", e);
        }
    }
}
