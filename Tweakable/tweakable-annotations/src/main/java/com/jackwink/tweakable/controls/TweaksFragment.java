package com.jackwink.tweakable.controls;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;

import com.jackwink.tweakable.generators.java.PreferenceCategoryBuilder;
import com.jackwink.tweakable.generators.java.PreferenceBuilder;
import com.jackwink.tweakable.generators.java.PreferenceScreenBuilder;
import com.jackwink.tweakable.parsers.TweakableAnnotationParser;
import com.jackwink.tweakable.types.AbstractTweakableValue;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 *
 */
public class TweaksFragment extends PreferenceFragment {
    private static final String TAG = TweaksFragment.class.getSimpleName();

    LinkedHashMap<String, PreferenceScreen> mScreens = new LinkedHashMap<>();
    LinkedHashMap<String, PreferenceCategory> mPreferences = new LinkedHashMap<>();

    /* Used in generated code, do not use directly! */
    public interface PreferenceAnnotationProcessor {
        Collection<Bundle> getRootPreferences();
        Collection<Bundle> getRootCategories();
        Collection<Bundle> getDeclaredScreens();
        Collection<Bundle> getDeclaredCategories();
        Collection<Bundle> getDeclaredPreferences();
    }

    private PreferenceAnnotationProcessor mProcessor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mProcessor = (PreferenceAnnotationProcessor) Class.forName(
                    "com.jackwink.tweakable.GeneratedPreferences").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bundle screenResources = new Bundle();
        screenResources.putString(AbstractTweakableValue.BUNDLE_TITLE_KEY, "Tweakable Values");
        screenResources.putString(AbstractTweakableValue.BUNDLE_KEYATTR_KEY,
                TweakableAnnotationParser.ROOT_SCREEN_KEY);
        PreferenceScreen root = new PreferenceScreenBuilder()
                .setContext(getActivity())
                .setPreferenceManager(getPreferenceManager())
                .setBundle(screenResources)
                .build();

        mScreens.put(root.getKey(), root);

        for (Bundle bundle : mProcessor.getRootPreferences()) {
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
            root.addPreference(preference);
        }

        for (Bundle bundle : mProcessor.getRootCategories()) {
            PreferenceCategory category =  new PreferenceCategoryBuilder()
                    .setContext(getActivity())
                    .setBundle(bundle)
                    .build();
            Log.d(TAG, "Created category: " + category.getKey());
            Log.d(TAG, "Adding '" + category.getTitle() + "' to root.");
            root.addPreference(category);
            mPreferences.put(category.getKey(), category);
        }

        /* Generate all the subscreens */
        for (Bundle bundle : mProcessor.getDeclaredScreens()) {
            PreferenceScreen screen = new PreferenceScreenBuilder()
                    .setContext(getActivity())
                    .setPreferenceManager(getPreferenceManager())
                    .setBundle(bundle)
                    .build();
            Log.d(TAG, "Created sub-screen: " + screen.getTitle());
            root.addPreference(screen);
            mScreens.put(screen.getKey(), screen);
        }

        /* Generate all the categories */
        for (Bundle bundle : mProcessor.getDeclaredCategories()) {
            String screenKey = bundle.getString(AbstractTweakableValue.BUNDLE_SCREEN_KEY);
            PreferenceCategory category =  new PreferenceCategoryBuilder()
                    .setContext(getActivity())
                    .setBundle(bundle)
                    .build();
            Log.d(TAG, "Created category: " + category.getKey());
            Log.d(TAG, "Adding '" + category.getTitle() + "' to " + screenKey);
            mScreens.get(screenKey).addPreference(category);
            mPreferences.put(category.getKey(), category);
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
            String screenKey = categoryKey.split("\\.")[0];

            if (mPreferences.containsKey(categoryKey)) {
                Log.d(TAG, "Adding preference '"
                        + preference.getTitle()
                        + "' to "
                        + categoryKey);
                mPreferences.get(categoryKey).addPreference(preference);
            } else if (mScreens.containsKey(screenKey)) {
                Log.d(TAG, "Adding preference '"
                        + preference.getTitle()
                        + "' to "
                        + screenKey);
                mScreens.get(screenKey).addPreference(preference);
            } else {
                throw new FailedToBuildPreferenceException("No such category: "
                        + categoryKey
                        + " or screen: "
                        + screenKey);
            }
        }

        setPreferenceScreen(root);
    }


}
