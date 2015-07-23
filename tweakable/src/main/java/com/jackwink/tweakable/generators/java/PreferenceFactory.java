package com.jackwink.tweakable.generators.java;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;

import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Builds a {@link Preference} from a {@link Bundle}
 */

public class PreferenceFactory {
    private static final String TAG = PreferenceFactory.class.getSimpleName();

    static Set<BaseBuilder<Preference>> builders = new LinkedHashSet<>(5);
    static {
        builders.add(new ActionPreferenceBuilder());
        builders.add(new BooleanPreferenceBuilder());
        builders.add(new FloatPreferenceBuilder());
        builders.add(new IntegerPreferenceBuilder());
        builders.add(new StringPreferenceBuilder());
    }

    public PreferenceFactory() {
    }

    /**
     * Builds a Preference object, can be any of the following types:
     * <ul>
     *     <li>{@link EditTextPreference}</li>
     *     <li>{@link ListPreference}</li>
     *     <li>{@link com.jackwink.tweakable.controls.NumberPickerPreference}</li>
     *     <li>{@link com.jackwink.tweakable.controls.ActionPreference}</li>
     *     <li>{@link com.jackwink.tweakable.controls.SliderPreference}</li>
     *     <li>{@link SwitchPreference}</li>
     * </ul>
     * @return An android preference.
     */
    public Preference build(Class type, Context context, Map<String, Object> attributeMap,
                            Object defaultValue) {
        for (BaseBuilder<Preference> builder : builders) {
            for (Class cls : builder.getHandledTypes()) {
                if (cls.equals(type)) {
                    Preference preference = builder
                            .setContext(context)
                            .setBundle(attributeMap)
                            .setDefaultValue(defaultValue)
                            .build();
                    builder.reset();
                    return preference;
                }
            }
        }
        throw new FailedToBuildPreferenceException("Type: " + type.getName() + " not supported.");
    }
}
