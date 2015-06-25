package com.jackwink.tweakable.generators.java;

import android.app.Application;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.jackwink.tweakable.types.*;

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

        Bundle bundle = new Bundle();
        bundle.putString(AbstractTweakableValue.BUNDLE_KEYATTR_KEY, key);
        bundle.putString(AbstractTweakableValue.BUNDLE_TITLE_KEY, title);
        bundle.putString(AbstractTweakableValue.BUNDLE_SUMMARY_KEY, summary);
        bundle.putBoolean(AbstractTweakableValue.BUNDLE_DEFAULT_VALUE_KEY, defaultValue);
        bundle.putString(TweakableBoolean.BUNDLE_ON_SUMMARY_KEY, onSummary);
        bundle.putString(TweakableBoolean.BUNDLE_OFF_SUMMARY_KEY, offSummary);
        bundle.putString(TweakableBoolean.BUNDLE_ON_LABEL_KEY, onLabel);
        bundle.putString(TweakableBoolean.BUNDLE_OFF_LABEL_KEY, offLabel);
        Preference preference = new PreferenceBuilder()
                .setType(boolean.class)
                .setBundle(bundle)
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

        Bundle bundle = new Bundle();
        bundle.putString(PreferenceBuilder.BUNDLE_KEYATTR_KEY, key);
        bundle.putString(PreferenceBuilder.BUNDLE_TITLE_KEY, title);
        bundle.putString(PreferenceBuilder.BUNDLE_SUMMARY_KEY, summary);
        bundle.putBoolean(PreferenceBuilder.BUNDLE_DEFAULT_VALUE_KEY, defaultValue);
        Preference preference = new PreferenceBuilder()
                .setType(boolean.class)
                .setBundle(bundle)
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
