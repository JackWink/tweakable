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

import java.util.LinkedHashMap;

/**
 *
 */
public class TweaksFragment extends PreferenceFragment {
    private static final String TAG = TweaksFragment.class.getSimpleName();

    LinkedHashMap<String, PreferenceScreen> mScreens = new LinkedHashMap<>();
    LinkedHashMap<String, PreferenceCategory> mPreferences = new LinkedHashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TweakableAnnotationParser parser = new TweakableAnnotationParser();

        try {
            parser.parse(Class.forName("com.jackwink.tweakabledemo.Settings"), null);
        } catch (Exception e) {
            e.printStackTrace();
            return;
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

        /* Generate all the subscreens */
        for (Bundle bundle : parser.getScreens()) {
            PreferenceScreen screen = new PreferenceScreenBuilder()
                    .setContext(getActivity())
                    .setPreferenceManager(getPreferenceManager())
                    .setBundle(bundle)
                    .build();
            Log.d(TAG, "Created sub-screen: " + screen.getTitle());
            root.addPreference(screen);
            Log.i(TAG, "Putting: " + screen.getKey() + " in map.");
            mScreens.put(screen.getKey(), screen);
        }

        /* Generate all the categories */
        for (Bundle bundle : parser.getCategories()) {
            String screenKey = bundle.getString(AbstractTweakableValue.BUNDLE_SCREEN_KEY);
            PreferenceCategory category =  new PreferenceCategoryBuilder()
                    .setContext(getActivity())
                    .setBundle(bundle)
                    .build();
            Log.d(TAG, "Created category: " + category.getKey());
            if (screenKey.equals(TweakableAnnotationParser.ROOT_SCREEN_KEY)) {
                Log.d(TAG, "Adding '" + category.getTitle() + "' to root screen.");
                root.addPreference(category);
            } else {
                Log.d(TAG, "Adding '" + category.getTitle() + "' to " + screenKey);
                mScreens.get(screenKey).addPreference(category);
            }
            mPreferences.put(category.getKey(), category);
        }

        /* Generate all the preferences */
        for (Bundle bundle : parser.getPreferences()) {
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
            String[] keys = categoryKey.split("\\.");
            if (keys[0].equals(TweakableAnnotationParser.ROOT_SCREEN_KEY)
                    && keys[1].equals(TweakableAnnotationParser.ROOT_CATEGORY_KEY)) {
                Log.d(TAG, "No category or screen listed for '"
                        + preference.getTitle()
                        + "', adding to root");
                root.addPreference(preference);
            } else if (keys[1].equals(TweakableAnnotationParser.ROOT_CATEGORY_KEY)) {
                Log.d(TAG, "No category listed for '"
                        + preference.getTitle()
                        + "', adding to "
                        + keys[1]);
                mScreens.get(keys[1]).addPreference(preference);
            } else if (!mPreferences.containsKey(categoryKey)) {
                throw new FailedToBuildPreferenceException("No such category: " + categoryKey);
            } else {
                mPreferences.get(categoryKey).addPreference(preference);
            }
        }

        setPreferenceScreen(root);
    }


}
