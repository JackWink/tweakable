package com.jackwink.tweakable;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.types.AbstractTweakableValue;

import java.lang.reflect.Field;

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
            Class cls = null;
            String typeInfo = bundle.getString(AbstractTweakableValue.BUNDLE_TYPEINFO_KEY);
            try {
                cls = Class.forName(typeInfo);
            } catch (ClassNotFoundException e) {
                if (typeInfo.equals(boolean.class.getName())) {
                    cls = boolean.class;
                } else if (typeInfo.equals(int.class.getName())) {
                    cls = int.class;
                } else {
                    Log.e(TAG, "Class not found: " + typeInfo);
                    e.printStackTrace();
                    continue;
                }
            }

            /* Create default entries if they don't exist */
            String preferenceKey = bundle.getString(AbstractTweakableValue.BUNDLE_KEYATTR_KEY);
            if (!mSharedPreferences.contains(preferenceKey)) {
                // Create entry
                if (cls.equals(boolean.class) || cls.equals(Boolean.class)) {
                    mSharedPreferences.edit()
                            .putBoolean(preferenceKey, bundle.getBoolean(
                                    AbstractTweakableValue.BUNDLE_DEFAULT_VALUE_KEY))
                            .commit();
                } else if (cls.equals(int.class) || cls.equals(Integer.class)) {
                    mSharedPreferences.edit()
                            .putInt(preferenceKey, bundle.getInt(
                                    AbstractTweakableValue.BUNDLE_DEFAULT_VALUE_KEY))
                            .commit();
                } else if (cls.equals(String.class)) {
                    mSharedPreferences.edit()
                            .putString(preferenceKey, bundle.getString(
                                    AbstractTweakableValue.BUNDLE_DEFAULT_VALUE_KEY))
                            .commit();
                }
            }

            /* Set default / saved values init */
            String fieldName = preferenceKey.substring(preferenceKey.lastIndexOf('.') + 1);
            String clsName = preferenceKey.substring(0, preferenceKey.lastIndexOf('.'));
            Log.i(TAG, "Updating '" + clsName + "' field: " + fieldName);

            Field field = null;
            try {
                field = Class.forName(clsName).getDeclaredField(fieldName);

                if (field.getType().equals(boolean.class)) {
                    field.setBoolean(null, mSharedPreferences.getBoolean(preferenceKey, false));
                } else if (field.getType().equals(Boolean.class)) {
                    field.set(null, mSharedPreferences.getBoolean(preferenceKey, false));
                } else if (field.getType().equals(int.class)) {
                    field.setInt(null, mSharedPreferences.getInt(preferenceKey, 0));
                } else if (field.getType().equals(Integer.class)) {
                    field.set(null, mSharedPreferences.getInt(preferenceKey, 0));
                } else if (field.getType().equals(String.class)) {
                    field.set(null, mSharedPreferences.getString(preferenceKey, ""));
                }
            } catch (ClassNotFoundException error) {
                error.printStackTrace();
            } catch (NoSuchFieldException error) {
                error.printStackTrace();
            } catch (IllegalAccessException error) {
                error.printStackTrace();
            }
        }
    }

    /**
     * <p>Returns an instance of the generated preferences class, or throws an exception if it can't
     * be found. For internal use only!</p>
     * @return The generated {@link TweaksFragment.PreferenceAnnotationProcessor}
     */
    static protected TweaksFragment.PreferenceAnnotationProcessor getPreferences() {
        try {
            return (TweaksFragment.PreferenceAnnotationProcessor)
                    Class.forName("com.jackwink.tweakable.GeneratedPreferences").newInstance();
        } catch (Exception e) {
            throw new FailedToBuildPreferenceException("Could not find generated preferences.", e);
        }
    }
}
