package com.jackwink.tweakable.generators.java;

import android.app.Application;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.LinkedHashMap;

public class PreferenceBuilderTest extends ApplicationTestCase<Application> {
    public PreferenceBuilderTest() {
        super(Application.class);
    }

    @SmallTest
    public void testNewBooleanWithAllAttributes() {
        String key = "test";
        String title = "I am a title!";
        String summary = "I am a summary";
        String onLabel = "ON";
        String offLabel = "OFF";
        String onSummary = "Switch is on!";
        String offSummary = "Switch is off!";
        boolean defaultValue = true;

        LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();
        objectMap.put(PreferenceBuilder.KEY_ATTRIBUTE, key);
        objectMap.put(PreferenceBuilder.TITLE_ATTRIBUTE, title);
        objectMap.put(PreferenceBuilder.SUMMARY_ATTRIBUTE, summary);
        objectMap.put(PreferenceBuilder.DEFAULT_VALUE_ATTRIBUTE, defaultValue);
        objectMap.put(PreferenceBuilder.ON_SUMMARY_ATTRIBUTE, onSummary);
        objectMap.put(PreferenceBuilder.OFF_SUMMARY_ATTRIBUTE, offSummary);
        objectMap.put(PreferenceBuilder.ON_LABEL_ATTRIBUTE, onLabel);
        objectMap.put(PreferenceBuilder.OFF_LABEL_ATTRIBUTE, offLabel);
        Preference preference = new PreferenceBuilder()
                .setType(boolean.class)
                .setAttributeMap(objectMap)
                .setContext(getContext())
                .build();

        assertEquals(preference instanceof SwitchPreference, true);

        SwitchPreference cast = (SwitchPreference) preference;
        assertEquals(key, cast.getKey());
        assertEquals(title, cast.getTitle());
        assertEquals(summary, cast.getSummary());
        assertEquals(defaultValue, cast.isChecked());
        assertEquals(onSummary, cast.getSummaryOn());
        assertEquals(offSummary, cast.getSummaryOff());
        assertEquals(onLabel, cast.getSwitchTextOn());
        assertEquals(offLabel, cast.getSwitchTextOff());
        assertEquals(true, cast.isPersistent());
    }

    @SmallTest
    public void testNewBooleanWithRequiredAttributes() {
        String key = "test";
        String title = "I am a title!";
        String summary = "I am a summary";
        boolean defaultValue = true;

        LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();
        objectMap.put(PreferenceBuilder.KEY_ATTRIBUTE, key);
        objectMap.put(PreferenceBuilder.TITLE_ATTRIBUTE, title);
        objectMap.put(PreferenceBuilder.SUMMARY_ATTRIBUTE, summary);
        objectMap.put(PreferenceBuilder.DEFAULT_VALUE_ATTRIBUTE, defaultValue);
        Preference preference = new PreferenceBuilder()
                .setType(boolean.class)
                .setAttributeMap(objectMap)
                .setContext(getContext())
                .build();

        assertEquals(preference instanceof SwitchPreference, true);

        SwitchPreference cast = (SwitchPreference) preference;
        assertEquals(key, cast.getKey());
        assertEquals(title, cast.getTitle());
        assertEquals(summary, cast.getSummary());
        assertEquals(defaultValue, cast.isChecked());
        assertEquals(null, cast.getSummaryOn());
        assertEquals(null, cast.getSummaryOff());
        assertEquals(null, cast.getSwitchTextOff());
        assertEquals(null, cast.getSwitchTextOn());
        assertEquals(true, cast.isPersistent());
    }
}
