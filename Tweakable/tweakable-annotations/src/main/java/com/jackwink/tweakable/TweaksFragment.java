package com.jackwink.tweakable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.jackwink.tweakable.generators.java.PreferenceCategoryBuilder;
import com.jackwink.tweakable.generators.java.PreferenceBuilder;
import com.jackwink.tweakable.generators.java.PreferenceScreenBuilder;
import com.jackwink.tweakable.types.AbstractTweakableValue;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 *
 */
public class TweaksFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = TweaksFragment.class.getSimpleName();

    LinkedHashMap<String, PreferenceScreen> mScreens = new LinkedHashMap<>();
    LinkedHashMap<String, PreferenceCategory> mCategories = new LinkedHashMap<>();

    private PreferenceAnnotationProcessor mProcessor;

    /* Used in generated code, do not use directly! */
    public interface PreferenceAnnotationProcessor {
        Collection<Bundle> getDeclaredScreens();
        Collection<Bundle> getDeclaredCategories();
        Collection<Bundle> getDeclaredPreferences();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProcessor = Tweakable.getPreferences();
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
            String key = bundle.getString(AbstractTweakableValue.BUNDLE_KEYATTR_KEY);
            //noinspection unchecked
            Preference preference = new PreferenceBuilder()
                    .setBundle(bundle)
                    .setContext(getActivity())
                    .setType(Tweakable.getType(key))
                    .setDefaultValue(Tweakable.getValue(key))
                    .build();

            String categoryKey = bundle.getString(AbstractTweakableValue.BUNDLE_CATEGORY_KEY);
            Log.d(TAG, "Adding preference '" + preference.getTitle() + "' to " + categoryKey);
            mCategories.get(categoryKey).addPreference(preference);
        }

        setPreferenceScreen(getRootScreen());
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
       Tweakable.bindValue(key, sharedPreferences.getAll().get(key));
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

    private PreferenceScreen getRootScreen() {
        return mScreens.get(com.jackwink.tweakable.types.AbstractTweakableValue.ROOT_SCREEN_KEY);
    }

}
