package com.jackwink.tweakable.parsers;

import android.os.Bundle;
import android.util.Log;

import com.jackwink.tweakable.controls.TweaksFragment;
import com.jackwink.tweakable.stores.ValueStore;
import com.jackwink.tweakable.types.AbstractTweakableValue;
import com.jackwink.tweakable.types.TweakableBoolean;
import com.jackwink.tweakable.types.TweakableValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 */
public class TweakableAnnotationParser implements TweaksFragment.PreferenceAnnotationProcessor {
    private static final String TAG = TweakableAnnotationParser.class.getSimpleName();

    public static final String ROOT_SCREEN_TITLE = "Tweakable Values";
    public static final String ROOT_SCREEN_KEY = "tweakable-values-root-screen";
    public static final String ROOT_CATEGORY_KEY = "tweakable-values-root-category";


    public static final String SCREEN_KEY_POSTFIX = "-screen";
    public static final String CATEGORY_KEY_POSTFIX = "-category";

    // Total number of tweakable values, just so it doesn't get senseless.
    private final static int MAX_TWEAKABLE_VALUES = 64;
    private final static int MAX_TWEAKABLE_CATEGORIES = 16;
    private final static int MAX_TWEAKABLE_SCREENS = 16;

    // TODO: init store
    private ValueStore mValueStore;

    // List of preferences
    private static LinkedHashSet<Bundle> mPreferences =
            new LinkedHashSet<>(MAX_TWEAKABLE_VALUES);

    private static LinkedHashMap<String, Bundle> mCategories =
            new LinkedHashMap<>(MAX_TWEAKABLE_CATEGORIES);

    private static LinkedHashMap<String, Bundle> mScreens =
            new LinkedHashMap<>(MAX_TWEAKABLE_SCREENS);

    public void parse(Class<?> cls, Object instance) throws Exception {
        Field[] fields = cls.getFields();

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
                    error.printStackTrace();
                    continue;
                }

                Bundle bundle = value.toBundle();
                if (!isScreenCreated(bundle)) {
                    createScreenBundle(bundle);
                }

                if (!isCategoryCreated(bundle)) {
                    createCategoryBundle(bundle);
                }

                bundle.putString(AbstractTweakableValue.BUNDLE_CATEGORY_KEY,
                        getCategoryKey(bundle));
                bundle.putString(AbstractTweakableValue.BUNDLE_SCREEN_KEY, getScreenKey(bundle));



                mPreferences.add(bundle);
            }
        }
    }

    @Override
    public Collection<Bundle> getRootPreferences() {
        return null;
    }

    @Override
    public Collection<Bundle> getRootCategories() {
        return null;
    }

    @Override
    public Collection<Bundle> getDeclaredScreens() {
        return mScreens.values();
    }

    @Override
    public Collection<Bundle> getDeclaredCategories() {
        return mCategories.values();
    }

    @Override
    public Collection<Bundle> getDeclaredPreferences() {
        return mPreferences;
    }

    private boolean isCategoryCreated(Bundle bundle) {
        if (!bundle.containsKey(AbstractTweakableValue.BUNDLE_CATEGORY_KEY)) {
            return true;
        }
        return mCategories.containsKey(getCategoryKey(bundle));
    }

    private void createCategoryBundle(Bundle bundle) {
        Bundle category = new Bundle();

        String categoryTitle = bundle.getString(AbstractTweakableValue.BUNDLE_CATEGORY_KEY);

        category.putString(AbstractTweakableValue.BUNDLE_TITLE_KEY, categoryTitle);
        category.putString(AbstractTweakableValue.BUNDLE_KEYATTR_KEY, getCategoryKey(bundle));
        category.putString(AbstractTweakableValue.BUNDLE_SCREEN_KEY, getScreenKey(bundle));
        mCategories.put(getCategoryKey(bundle), category);
    }

    private String getCategoryKey(Bundle bundle) {
        String screenKey = getScreenKey(bundle);
        String categoryTitle = bundle.getString(AbstractTweakableValue.BUNDLE_CATEGORY_KEY);
        if (categoryTitle != null) {
            return screenKey + "." + normalize(categoryTitle) + CATEGORY_KEY_POSTFIX;
        }
        return screenKey + "." + ROOT_CATEGORY_KEY;
    }

    private String getScreenKey(Bundle bundle) {
        String screenKey = bundle.getString(AbstractTweakableValue.BUNDLE_SCREEN_KEY);
        if (screenKey == null) {
            return ROOT_SCREEN_KEY;
        }
        return normalize(screenKey) + SCREEN_KEY_POSTFIX;
    }

    private String normalize(String str) {
        return str.replace(' ', '-').replace('.', '-').toLowerCase();
    }

    private boolean isScreenCreated(Bundle bundle) {
        if (!bundle.containsKey(AbstractTweakableValue.BUNDLE_SCREEN_KEY)) {
            return true;
        }
        return mScreens.containsKey(getScreenKey(bundle));
    }

    private void createScreenBundle(Bundle bundle) {
        Bundle screen = new Bundle();
        String screenTitle = bundle.getString(AbstractTweakableValue.BUNDLE_SCREEN_KEY);
        String screenKey = getScreenKey(bundle);
        Log.i(TAG, "Creating screen bundle for: " + screenKey);

        screen.putString(AbstractTweakableValue.BUNDLE_TITLE_KEY, screenTitle);
        screen.putString(AbstractTweakableValue.BUNDLE_KEYATTR_KEY, screenKey);
        mScreens.put(screenKey, screen);
    }

}
