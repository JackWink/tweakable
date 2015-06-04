package com.jackwink.tweakable.generators.java;

import android.app.Application;
import android.preference.Preference;
import android.preference.PreferenceCategory;

import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PreferenceCategoryBuilderTest extends ApplicationTestCase {

    public PreferenceCategoryBuilderTest() {
        super(Application.class);
    }


    @SmallTest
    public void testNewCategoryWithAllAttributes() {
        String key = "testCategory";
        String title = "Test Category";

        LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();
        objectMap.put(PreferenceCategoryBuilder.KEY_ATTRIBUTE, key);
        objectMap.put(PreferenceCategoryBuilder.TITLE_ATTRIBUTE, title);
        
        PreferenceCategory category = new PreferenceCategoryBuilder()
                .setAttributeMap(objectMap)
                .setContext(getContext())
                .build();

        assertEquals(key, category.getKey());
        assertEquals(title, category.getTitle());
    }
}
