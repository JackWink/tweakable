package com.jackwink.tweakable;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.jackwink.tweakable.controls.TweaksFragment;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.types.AbstractTweakableValue;

import java.lang.reflect.Field;

/**
 *
 */
public class Tweakable {
    private static final String TAG = Tweakable.class.getSimpleName();

    private static String mSharedPreferencesName;
    private static SharedPreferences mSharedPreferences;

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
                if (cls.equals(boolean.class)) {
                    mSharedPreferences.edit()
                            .putBoolean(preferenceKey, bundle.getBoolean(
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

                if (field.getType().getName().equals(boolean.class.getName())) {
                    field.setBoolean(null, mSharedPreferences.getBoolean(preferenceKey, false));
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

    public static TweaksFragment.PreferenceAnnotationProcessor getPreferences() {
        try {
            return (TweaksFragment.PreferenceAnnotationProcessor)
                    Class.forName("com.jackwink.tweakable.GeneratedPreferences").newInstance();
        } catch (Exception e) {
            throw new FailedToBuildPreferenceException("Could not find generated preferences.", e);
        }
    }
}
