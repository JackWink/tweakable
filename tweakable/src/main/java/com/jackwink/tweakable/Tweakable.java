package com.jackwink.tweakable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import com.jackwink.tweakable.binders.AbstractValueBinder;
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
        mSharedPreferences = context.getSharedPreferences(mSharedPreferencesName,
                Context.MODE_PRIVATE);
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mShakeListener = new TweakShakeListener();
        mShakeDetector = new ShakeDetector(mShakeListener);

        for (Map<String, Object> bundle: getPreferences().getDeclaredPreferences()) {
            String preferenceKey = (String) bundle.get(AbstractTweakableValue.BUNDLE_KEYATTR_KEY);

            /* Set default / saved values init */
            String fieldName = preferenceKey.substring(preferenceKey.lastIndexOf('.') + 1);
            String clsName = preferenceKey.substring(0, preferenceKey.lastIndexOf('.'));
            Log.i(TAG, "Updating '" + clsName + "' field: " + fieldName);

            AbstractValueBinder binder = null;
            Field field = null;
            Method action = null;
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
                } else if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
                    binder = new FloatValueBinder(field);
                    ((FloatValueBinder) binder).bindValue(mSharedPreferences.getFloat(
                            preferenceKey, (Float) binder.getValue()));
                } else {
                    throw new FailedToBuildPreferenceException(
                            "No value binder exists for: " + field.getType().toString());
                }
                Log.i(TAG, "Value: " + binder.getValue());
                mValueBinders.put(preferenceKey, binder);
            } catch (NoSuchFieldException e) {
                try {
                    Method method = Class.forName(clsName).getMethod(fieldName);
                    binder = new ActionBinder(method);
                    mValueBinders.put(preferenceKey, binder);
                } catch (Exception methodError) {
                    throw new FailedToBuildPreferenceException("No such method", methodError);
                }
            } catch (Exception error) {
                throw new FailedToBuildPreferenceException("Failed to build value binders.", error);
            }
        }

        if (mOnShakeEnabled) {
            startShakeListener();
        }

        if (Build.FINGERPRINT.startsWith("generic")) {
            mShakeListener.hearShake();
        }
    }

    protected static void bindValue(String key, Object value) {
        if (mValueBinders.containsKey(key)) {
            ValueBinder binder = mValueBinders.get(key);
            if (binder.getType().equals(Method.class)) {
                binder.bindValue(binder.getValue());
            } else {
                binder.bindValue(value);
            }
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
