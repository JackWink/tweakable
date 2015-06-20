package com.jackwink.tweakable.parsers;

import android.os.Bundle;

import com.jackwink.tweakable.annotations.TweakableInteger;
import com.jackwink.tweakable.generators.xml.PreferenceBuilder;
import com.jackwink.tweakable.stores.ValueStore;
import com.jackwink.tweakable.types.PreferenceEntry;
import com.jackwink.tweakable.types.TweakableBoolean;
import com.jackwink.tweakable.types.TweakableValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;

/**
 *
 */
public class TweakableAnnotationParser {
    // Total number of tweakable values, just so it doesn't get senseless.
    private final static int MAX_TWEAKABLE_VALUES = 64;
    private final static int MAX_TWEAKABLE_CATEGORIES = 16;

    // TODO: init store
    private ValueStore mValueStore;

    // List of preferences
    private static LinkedHashMap<String, TweakableValue> mPreferences =
            new LinkedHashMap<>(MAX_TWEAKABLE_VALUES);

    private static LinkedHashMap<String, PreferenceEntry> mCategories =
            new LinkedHashMap<>(MAX_TWEAKABLE_CATEGORIES);

    public void parse(Class<?> cls, Object instance) throws Exception {
        Field[] fields = cls.getFields();

        String fieldName;
        String categoryName;
        for (Field field : fields) {
            // Skip all non-static fields for now, as well as non-annotated methods.
            // TODO: See if static is an artificial limitation -- I think it'll be a lot of work for
            //       non-static fields.
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            TweakableValue value = null;

            if (field.getType().equals(boolean.class)) {
                try {
                    value = TweakableBoolean.parse(cls, field);
                } catch (IllegalArgumentException error) {
                    continue;
                }
            }

        }
    }

}
