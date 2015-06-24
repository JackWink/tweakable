package com.jackwink.tweakable.controls;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.jackwink.tweakable.binders.AbstractValueBinder;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;

import com.jackwink.tweakable.generators.java.PreferenceCategoryBuilder;
import com.jackwink.tweakable.generators.java.PreferenceBuilder;
import com.jackwink.tweakable.generators.java.PreferenceScreenBuilder;
import com.jackwink.tweakable.types.AbstractTweakableValue;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 *
 */
public class TweaksFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = TweaksFragment.class.getSimpleName();

    LinkedHashMap<String, PreferenceScreen> mScreens = new LinkedHashMap<>();
    LinkedHashMap<String, PreferenceCategory> mCategories = new LinkedHashMap<>();
    LinkedHashMap<String, AbstractValueBinder> mPreferences = new LinkedHashMap<>();

    private PreferenceAnnotationProcessor mProcessor;

    /* Used in generated code, do not use directly! */
    public interface PreferenceAnnotationProcessor {
        Collection<Bundle> getDeclaredScreens();
        Collection<Bundle> getDeclaredCategories();
        Collection<Bundle> getDeclaredPreferences();
    }


    /**
     * Creates Root preference screen and the root category.
     */
    private void createRootElements() {
        Bundle rootScreenBundle = new Bundle();
        rootScreenBundle.putString(AbstractTweakableValue.BUNDLE_TITLE_KEY, "Tweakable Values");
        rootScreenBundle.putString(AbstractTweakableValue.BUNDLE_KEYATTR_KEY,
                AbstractTweakableValue.ROOT_SCREEN_KEY);

        Bundle rootCategoryBundle = new Bundle();
        rootCategoryBundle.putString(AbstractTweakableValue.BUNDLE_TITLE_KEY, "");
        rootCategoryBundle.putString(AbstractTweakableValue.BUNDLE_KEYATTR_KEY,
                AbstractTweakableValue.ROOT_SCREEN_KEY + "."
                        + AbstractTweakableValue.ROOT_CATEGORY_KEY);

        PreferenceScreen rootScreen = new PreferenceScreenBuilder()
                .setContext(getActivity())
                .setPreferenceManager(getPreferenceManager())
                .setBundle(rootScreenBundle)
                .build();

        PreferenceCategory rootCategory = new PreferenceCategoryBuilder()
                .setContext(getActivity())
                .setBundle(rootCategoryBundle)
                .build();

        rootScreen.addPreference(rootCategory);
        mScreens.put(rootScreen.getKey(), rootScreen);
        mCategories.put(rootCategory.getKey(), rootCategory);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mProcessor = (PreferenceAnnotationProcessor) Class.forName(
                    "com.jackwink.tweakable.GeneratedPreferences").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        createRootElements();

        /* Generate all the subscreens */
        for (Bundle bundle : mProcessor.getDeclaredScreens()) {
            PreferenceScreen screen = new PreferenceScreenBuilder()
                    .setContext(getActivity())
                    .setPreferenceManager(getPreferenceManager())
                    .setBundle(bundle)
                    .build();
            Log.d(TAG, "Created sub-screen: " + screen.getTitle());
            getRootScreen().addPreference(screen);
            mScreens.put(screen.getKey(), screen);

            Bundle rootCategoryBundle = new Bundle();
            rootCategoryBundle.putString(AbstractTweakableValue.BUNDLE_TITLE_KEY, "");
            rootCategoryBundle.putString(AbstractTweakableValue.BUNDLE_KEYATTR_KEY,
                    screen.getKey() + "." + AbstractTweakableValue.ROOT_CATEGORY_KEY);
            PreferenceCategory rootCategory = new PreferenceCategoryBuilder()
                    .setContext(getActivity())
                    .setBundle(rootCategoryBundle)
                    .build();
            screen.addPreference(rootCategory);
            mCategories.put(rootCategory.getKey(), rootCategory);
        }

        /* Generate all the categories */
        for (Bundle bundle : mProcessor.getDeclaredCategories()) {
            String screenKey = bundle.getString(AbstractTweakableValue.BUNDLE_SCREEN_KEY);
            PreferenceCategory category = new PreferenceCategoryBuilder()
                    .setContext(getActivity())
                    .setBundle(bundle)
                    .build();
            Log.d(TAG, "Created category: " + category.getKey());
            Log.d(TAG, "Adding '" + category.getTitle() + "' to " + screenKey);
            mScreens.get(screenKey).addPreference(category);
            mCategories.put(category.getKey(), category);
        }

        /* Generate all the preferences */
        for (Bundle bundle : mProcessor.getDeclaredPreferences()) {
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

            //noinspection unchecked
            Preference preference = new PreferenceBuilder()
                    .setBundle(bundle)
                    .setContext(getActivity())
                    .setType(cls)
                    .build();

            String categoryKey = bundle.getString(AbstractTweakableValue.BUNDLE_CATEGORY_KEY);
            Log.d(TAG, "Adding preference '" + preference.getTitle() + "' to " + categoryKey);
            mCategories.get(categoryKey).addPreference(preference);

            if (cls.equals(boolean.class)) {
                if (!getRootScreen().getSharedPreferences().contains(preference.getKey())) {
                    Log.i(TAG, "Creating preference...");
                    getRootScreen().getSharedPreferences().edit().putBoolean(preference.getKey(),
                            bundle.getBoolean(AbstractTweakableValue.BUNDLE_DEFAULT_VALUE_KEY))
                            .commit();
                }
            }
            onSharedPreferenceChanged(getRootScreen().getSharedPreferences(), preference.getKey());
        }

        setPreferenceScreen(getRootScreen());
    }



    private PreferenceScreen getRootScreen() {
        return mScreens.get(AbstractTweakableValue.ROOT_SCREEN_KEY);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG, "Preference change: " + key);
        String fieldName = key.substring(key.lastIndexOf('.') + 1);
        String clsName = key.substring(0, key.lastIndexOf('.'));
        Log.i(TAG, "Updating '" + clsName + "' field: " + fieldName);

        Field field = null;
        try {
            field = Class.forName(clsName).getDeclaredField(fieldName);

            if (field.getType().getName().equals(boolean.class.getName())) {
                field.setBoolean(null, sharedPreferences.getBoolean(key, false));
                Log.i(TAG, "Set value: " + sharedPreferences.getBoolean(key, false));
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
