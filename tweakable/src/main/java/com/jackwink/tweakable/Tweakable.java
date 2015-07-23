package com.jackwink.tweakable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import com.jackwink.tweakable.binders.ActionBinder;
import com.jackwink.tweakable.binders.BooleanValueBinder;
import com.jackwink.tweakable.binders.FloatValueBinder;
import com.jackwink.tweakable.binders.IntegerValueBinder;
import com.jackwink.tweakable.binders.StringValueBinder;
import com.jackwink.tweakable.binders.ValueBinder;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.types.AbstractTweakableValue;

import com.squareup.seismic.ShakeDetector;

import java.lang.reflect.Field;
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
    private static Context mContext;

    private static boolean mOnShakeEnabled;
    private static ShakeDetector mShakeDetector;
    private static TweakShakeListener mShakeListener;
    private static SensorManager mSensorManager;

    private static LinkedHashMap<String, ValueBinder> mValueBinders =
            new LinkedHashMap<>();


    public static void init(Context context) {
        init(context, true);
    }

    /**
     * Binds the default values (or saved values) to all annotated fields.
     *
     * @param context Application or activity context used to retrieve the shared preferences file.
     * @param onShake True if you want the TweaksActivity to start on shake, false otherwise.
     */
    public static void init(Context context, boolean onShake) {
        mContext = context;
        mOnShakeEnabled = onShake;
        mSharedPreferencesName = context.getPackageName() + "_preferences";
        mSharedPreferences = context.getSharedPreferences(
                mSharedPreferencesName, Context.MODE_PRIVATE);
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mShakeListener = new TweakShakeListener();
        mShakeDetector = new ShakeDetector(mShakeListener);

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

        if (Build.FINGERPRINT.startsWith("generic")) {
            mShakeListener.hearShake();
        }
    }

    private static ValueBinder createBinder(String clsName, String fieldName) {
        Field field = null;
        try {
            field = Class.forName(clsName).getDeclaredField(fieldName);
            if (contains(BooleanValueBinder.DECLARED_TYPES, field.getType())) {
                return new BooleanValueBinder(field);
            } else if (contains(IntegerValueBinder.DECLARED_TYPES, field.getType())) {
                return new IntegerValueBinder(field);
            } else if (contains(StringValueBinder.DECLARED_TYPES, field.getType())) {
                return new StringValueBinder(field);
            } else if (contains(FloatValueBinder.DECLARED_TYPES, field.getType())) {
                return new FloatValueBinder(field);
            }
        } catch (NoSuchFieldException e) {
            return createMethodBinder(clsName, fieldName);
        } catch (ClassNotFoundException error) {
            throw new FailedToBuildPreferenceException("Class not found: " + clsName, error);
        }

        throw new FailedToBuildPreferenceException("Unhandled field type: " + field.getType());
    }

    private static ValueBinder createMethodBinder(String clsName, String methodName) {
        try {
            Method method = Class.forName(clsName).getMethod(methodName);
            return new ActionBinder(method);
        } catch (Exception e) {
            throw new FailedToBuildPreferenceException("No such method: " + methodName, e);
        }
    }

    private static boolean contains(Class[] classList, Class cls) {
        for (Class clazz : classList) {
            if (clazz.equals(cls)) {
                return true;
            }
        }
        return false;
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

    protected static synchronized void disableShakeListener() {
        mShakeDetector.stop();
        mOnShakeEnabled = false;
    }

    protected static synchronized void startShakeListener() {
        mOnShakeEnabled = true;
        mShakeDetector.start(mSensorManager);
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

    private static class TweakShakeListener implements ShakeDetector.Listener {
        @Override
        public synchronized void hearShake() {
            if (mOnShakeEnabled) {
                Tweakable.disableShakeListener();
                Intent settingsIntent = new Intent(mContext, TweaksActivity.class);
                settingsIntent.putExtra(TweaksActivity.EXTRA_SHOW_FRAGMENT,
                        TweaksFragment.class.getCanonicalName());
                settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(settingsIntent);
            }
        }
    }

}
