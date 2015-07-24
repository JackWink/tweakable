package com.jackwink.tweakable;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.jackwink.tweakable.binders.ActionBinder;
import com.jackwink.tweakable.binders.ValueBinder;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.types.AbstractTweakableValue;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tweakable values and Feature Flags for Android.
 *
 * <h2>Usage Instructions</h2>
 *
 * <p>Call {@link Tweakable#init(Context)} to inject the default values
 * (or saved values from Preferences) on the start of your application or main activity.</p>
 *
 * <p>Shake your phone to pull up the generated settings!</p>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Tweakable {
    private static final String TAG = Tweakable.class.getSimpleName();

    private static String mSharedPreferencesName;
    private static SharedPreferences mSharedPreferences;

    private static TweakableShakeDetector mShakeDetector;
    private static boolean mOnShakeEnabled;

    private static LinkedHashMap<String, ValueBinder> mValueBinders =
            new LinkedHashMap<>();

    /**
     * Binds the default values (or saved values) to all annotated fields and enables shake
     * detection in your app.  For release builds, it's suggested to not call init to avoid
     * a performance hit.
     *
     * @param context
     */
    public static void init(Context context) {
        init(context, true);
    }

    /**
     * Binds the default values (or saved values) to all annotated fields. If startOnShake is true,
     * then this enables shake detection in your app.  If you wish to launch the
     * {@link TweaksActivity} on your own, you should pass in false.
     *
     * @param context Application context used to retrieve the shared preferences file.
     * @param startOnShake True if you want the TweaksActivity to start on shake, false otherwise.
     */
    public static void init(Context context, boolean startOnShake) {
        mShakeDetector = new TweakableShakeDetector(context);
        mOnShakeEnabled = startOnShake;
        mSharedPreferencesName = context.getPackageName() + "_preferences";
        mSharedPreferences = context.getSharedPreferences(
                mSharedPreferencesName, Context.MODE_PRIVATE);

        for (Map<String, Object> bundle : getPreferences().getDeclaredPreferences()) {
            String preferenceKey = (String) bundle.get(AbstractTweakableValue.BUNDLE_KEYATTR_KEY);
            String fieldName = preferenceKey.substring(preferenceKey.lastIndexOf('.') + 1);
            String clsName = preferenceKey.substring(0, preferenceKey.lastIndexOf('.'));
            ValueBinder binder = createBinder(clsName, fieldName);
            // We don't want to call actions on init, but we want to restore values from prefs
            if (!(binder instanceof ActionBinder)) {
                Log.i(TAG, "Updating '" + clsName + "' field: " + fieldName);
                binder.bindValue(mSharedPreferences, preferenceKey);
            }
            mValueBinders.put(preferenceKey, binder);
        }

        if (mOnShakeEnabled) {
            startShakeListener();
        }
    }

    protected static void bindValue(String key, Object value) {
        if (mValueBinders.containsKey(key)) {
            mValueBinders.get(key).bindValue(mSharedPreferences, key);
        }
    }

    protected static Class getType(String key) {
        return mValueBinders.get(key).getType();
    }

    protected static Object getValue(String key) {
        return mValueBinders.get(key).getValue();
    }

    protected static synchronized void resetShakeListener() {
        if (!mOnShakeEnabled) {
            Log.w(TAG, "Shake detection not enabled!");
        }
        mShakeDetector.reset();
    }

    protected static synchronized boolean isShakeListenerEnabled() {
        return mOnShakeEnabled;
    }

    protected static synchronized void startShakeListener() {
        if (mOnShakeEnabled) {
            Log.w(TAG, "Shake detection not enabled!");
        }
        mShakeDetector.listen();
    }

    /**
     * <p>Returns an instance of the generated preferences class, or throws an exception if it can't
     * be found. For internal use only!</p>
     * @return The generated {@link PreferenceAnnotationProcessor}
     */
    protected static PreferenceAnnotationProcessor getPreferences() {
        try {
            return (PreferenceAnnotationProcessor)
                    Class.forName("com.jackwink.tweakable.GeneratedPreferences").newInstance();
        } catch (Exception e) {
            throw new FailedToBuildPreferenceException("Could not find generated preferences.", e);
        }
    }

    private static ValueBinder createBinder(String clsName, String fieldName) {
        Member fieldOrMethod = null;
        Class type = null;
        try {
            fieldOrMethod = Class.forName(clsName).getDeclaredField(fieldName);
            type = ((Field) fieldOrMethod).getType();
        } catch (NoSuchFieldException e) {
            try {
                fieldOrMethod = Class.forName(clsName).getDeclaredMethod(fieldName);
                type = Method.class;
            } catch (Exception methodException) {
                throw new FailedToBuildPreferenceException(
                        "Couldn't create binder for " + fieldName, methodException);
            }
        } catch (Exception e) {
            throw new FailedToBuildPreferenceException("Couldn't create binder for: " + clsName, e);
        }

        for (ValueBinder.DeclaredTypes binderType : ValueBinder.DeclaredTypes.values()) {
            if (binderType.contains(type)) {
                return binderType.getBinder(fieldOrMethod);
            }
        }

        throw new FailedToBuildPreferenceException("Unhandled field type: " + type);
    }
}
