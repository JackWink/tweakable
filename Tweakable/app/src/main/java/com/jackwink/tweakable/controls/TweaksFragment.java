package com.jackwink.tweakable.controls;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
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
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 */
public class TweaksFragment extends PreferenceFragment {
    private static final String TAG = TweaksFragment.class.getSimpleName();

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
        screenResources.putString(PreferenceScreenBuilder.EXTRA_TITLE, "My Settings!");
        PreferenceScreen root = new PreferenceScreenBuilder()
                .setContext(getActivity())
                .setPreferenceManager(getPreferenceManager())
                .setBundle(screenResources)
                .build();

        /* Generate all the categories */
        for (Bundle bundle : parser.getCategories()) {

            PreferenceCategory category =  new PreferenceCategoryBuilder()
                    .setContext(getActivity())
                    .setBundle(bundle)
                    .build();
            root.addPreference(category);
            Log.i(TAG, "Creating category: " + category.getTitle());
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

            String categoryKey = bundle.getString(AbstractTweakableValue.BUNDLE_CATEGORY_KEY) + "-category";
            bundle.remove(AbstractTweakableValue.BUNDLE_CATEGORY_KEY);

            //noinspection unchecked
            Preference preference = new PreferenceBuilder()
                    .setBundle(bundle)
                    .setContext(getActivity())
                    .setType(cls)
                    .build();

            if (categoryKey == null) {
                root.addPreference(preference);
            } else if (!mPreferences.containsKey(categoryKey)) {
                throw new FailedToBuildPreferenceException("No such category: " + categoryKey);
            } else {
                mPreferences.get(categoryKey).addPreference(preference);
            }
        }

        setPreferenceScreen(root);
    }


}
