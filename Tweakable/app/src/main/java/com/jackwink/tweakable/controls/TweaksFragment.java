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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 */
public class TweaksFragment extends PreferenceFragment {
    private static final String TAG = TweaksFragment.class.getSimpleName();

    Set<Bundle> mCategories = new LinkedHashSet<>();
    Set<Bundle> mProcessedAnnotations = new LinkedHashSet<>();

    LinkedHashMap<String, PreferenceCategory> mPreferences = new LinkedHashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle categoryResources = new Bundle();
        categoryResources.putString(PreferenceCategoryBuilder.BUNDLE_TITLE_KEY, "Boolean");
        categoryResources.putString(PreferenceCategoryBuilder.BUNDLE_KEYATTR_KEY, "boolean-category");
        mCategories.add(categoryResources);

        Bundle settingResource = new Bundle();
        settingResource.putString(PreferenceBuilder.BUNDLE_TITLE_KEY, "Light Switch");
        settingResource.putString(PreferenceBuilder.BUNDLE_TYPE_INFORMATION, boolean.class.getCanonicalName());
        settingResource.putString(PreferenceBuilder.BUNDLE_KEYATTR_KEY, "lightSwitch");
        settingResource.putString(PreferenceBuilder.BUNDLE_SUMMARY_KEY, "Turns the lightswitch on or off.");
        settingResource.putString("category_key", "boolean-category");
        settingResource.putBoolean(PreferenceBuilder.BUNDLE_DEFAULT_VALUE_KEY, true);
        settingResource.putString(PreferenceBuilder.ON_LABEL_ATTRIBUTE, "ON!");
        settingResource.putString(PreferenceBuilder.OFF_LABEL_ATTRIBUTE, "OFF!");
        settingResource.putString(PreferenceBuilder.ON_SUMMARY_ATTRIBUTE, "Light switch is on!");
        settingResource.putString(PreferenceBuilder.OFF_SUMMARY_ATTRIBUTE, "Light switch is off!");
        mProcessedAnnotations.add(settingResource);


        Bundle screenResources = new Bundle();
        screenResources.putString(PreferenceScreenBuilder.EXTRA_TITLE, "My Settings!");
        PreferenceScreen root = new PreferenceScreenBuilder()
                .setContext(getActivity())
                .setPreferenceManager(getPreferenceManager())
                .setBundle(screenResources)
                .build();

        /* Generate all the categories */
        for (Bundle bundle : mCategories) {
            PreferenceCategory category =  new PreferenceCategoryBuilder()
                    .setContext(getActivity())
                    .setBundle(bundle)
                    .build();
            root.addPreference(category);
            mPreferences.put(category.getKey(), category);
        }

        /* Generate all the preferences */
        for (Bundle bundle : mProcessedAnnotations) {
            Class cls = null;
            try {
                cls = Class.forName(bundle.getString("type_information"));
            } catch (ClassNotFoundException e) {
                if (bundle.getString("type_information").equals(boolean.class.getName())) {
                    cls = boolean.class;
                } else {
                    Log.e(TAG, "Class not found: " + bundle.getString("type_information"));
                    e.printStackTrace();
                    continue;
                }
            }

            String category = bundle.getString("category_key");
            bundle.remove("category_key");

            //noinspection unchecked
            Preference preference = new PreferenceBuilder()
                    .setBundle(bundle)
                    .setContext(getActivity())
                    .setType(cls)
                    .build();

            if (category == null) {
                root.addPreference(preference);
            } else if (!mPreferences.containsKey(category)) {
                throw new FailedToBuildPreferenceException("No such category: " + category);
            } else {
                mPreferences.get(category).addPreference(preference);
            }
        }

        setPreferenceScreen(root);
    }


}
