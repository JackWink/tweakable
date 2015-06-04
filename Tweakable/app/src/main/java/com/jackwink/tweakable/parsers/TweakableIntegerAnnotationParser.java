package com.jackwink.tweakable.parsers;

import com.jackwink.tweakable.annotations.TweakableInteger;
import com.jackwink.tweakable.generators.xml.PreferenceBuilder;
import com.jackwink.tweakable.stores.ValueStore;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;

/**
 *
 */
public class TweakableIntegerAnnotationParser {
    // Total number of tweakable values, just so it doesn't get senseless.
    private final static int MAX_TWEAKABLE_VALUES = 64;

    // TODO: init store
    private ValueStore mValueStore;

    // List of field details for settings view
    private static LinkedHashMap<String, ValueCache> mFields = new LinkedHashMap<>(MAX_TWEAKABLE_VALUES);

    public void parse(Class<?> cls, Object instance) throws Exception {
        Field[] fields = cls.getFields();

        String fieldName;
        String categoryName;
        for (Field field : fields) {
            // Skip all non-static fields for now, as well as non-annotated methods.
            // TODO: See if static is an artificial limitation -- I think it'll be a lot of work for
            //       non-static fields.
            if (!field.isAnnotationPresent(TweakableInteger.class) ||
                    !Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            TweakableInteger value = field.getAnnotation(TweakableInteger.class);

            // If the name provided from the annotation is not empty: use it, otherwise use
            // the field name.
            fieldName = !value.name().isEmpty() ? value.name() : field.getName();

            // If the category is specified by the annotation: use it, otherwise use the class name.
            categoryName = !value.category().isEmpty() ? value.category() : cls.getSimpleName();

            mFields.put(fieldName, new ValueCache(categoryName, value.range(), value.defaultsTo()));
            field.setInt(instance, mValueStore.getInt(fieldName, value.defaultsTo()));
        }
    }

    /**
     * Holds a copy of the annotation data so we don't have to re-parse the field each time.
     *
     * TODO: Perf test/limit this.
     */
    private class ValueCache {
        private String mCategory;
        private int[] mRange;
        private int mDefaultValue;

        public ValueCache(String category, int[] range, int defaultValue) {
            mCategory = category;
            mRange = range.clone();
            mDefaultValue = defaultValue;
            PreferenceBuilder builder = new PreferenceBuilder();
        }
    }
}
