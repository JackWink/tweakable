package com.jackwink.tweakable.parsers;

import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;

import com.jackwink.tweakable.annotations.TweakableInteger;
import com.jackwink.tweakable.generators.xml.PreferenceBuilder;
import com.jackwink.tweakable.stores.ValueStore;
import com.jackwink.tweakable.types.AbstractTweakableValue;
import com.jackwink.tweakable.types.PreferenceEntry;
import com.jackwink.tweakable.types.TweakableBoolean;
import com.jackwink.tweakable.types.TweakableValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class TweakableAnnotationParser {
    private static final String TAG = TweakableAnnotationParser.class.getSimpleName();

    // Total number of tweakable values, just so it doesn't get senseless.
    private final static int MAX_TWEAKABLE_VALUES = 64;
    private final static int MAX_TWEAKABLE_CATEGORIES = 16;

    // TODO: init store
    private ValueStore mValueStore;

    // List of preferences
    private static LinkedHashSet<Bundle> mPreferences =
            new LinkedHashSet<>(MAX_TWEAKABLE_VALUES);

    private static LinkedHashMap<String, Bundle> mCategories =
            new LinkedHashMap<>(MAX_TWEAKABLE_CATEGORIES);

    public void parse(Class<?> cls, Object instance) throws Exception {
        Field[] fields = cls.getFields();

        String fieldName;
        String categoryName;
        for (Field field : fields) {
            // Skip all non-static fields for now, as well as non-annotated methods.
            // TODO: See if static is an artificial limitation -- I think it'll be a lot of work for
            //       non-static fields.
            Log.d(TAG, "Checking: " + field.getName());
            if (!Modifier.isStatic(field.getModifiers())) {
                Log.d(TAG, "Not static, discarding: " + field.getName());
                continue;
            }

            TweakableValue value = null;

            if (field.getType().equals(boolean.class)) {
                try {
                    Log.d(TAG, "Parsing: " + field.getName() + " as boolean.");
                    value = TweakableBoolean.parse(cls, field);
                } catch (IllegalArgumentException error) {
                    continue;
                }

                Bundle bundle = value.toBundle();
                if (!isCategoryCreated(bundle)) {
                    createCategoryBundle(bundle);
                }
                mPreferences.add(bundle);
            }
        }
    }

    public Set<Bundle> getPreferences() {
        return mPreferences;
    }

    public Collection<Bundle> getCategories() {
        return mCategories.values();
    }

    private boolean isCategoryCreated(Bundle bundle) {
        if (!bundle.containsKey(AbstractTweakableValue.BUNDLE_CATEGORY_KEY)) {
            return true;
        }
        return mCategories.containsKey(bundle.getString(AbstractTweakableValue.BUNDLE_CATEGORY_KEY));
    }

    private void createCategoryBundle(Bundle bundle) {
        Bundle category = new Bundle();
        String categoryTitle = bundle.getString(AbstractTweakableValue.BUNDLE_CATEGORY_KEY);
        String categoryKey = categoryTitle + "-category";

        category.putString(AbstractTweakableValue.BUNDLE_TITLE_KEY, categoryTitle);
        category.putString(AbstractTweakableValue.BUNDLE_KEYATTR_KEY, categoryKey);
        mCategories.put(categoryKey, category);
    }

}
