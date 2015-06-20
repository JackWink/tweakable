package com.jackwink.tweakable.generators.java;

import android.app.Application;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class PreferenceCategoryBuilderTest extends ApplicationTestCase {

    public PreferenceCategoryBuilderTest() {
        super(Application.class);
    }

    @SmallTest
    public void testNewCategoryWithAllAttributes() {
        String key = "testCategory";
        String title = "Test Category";

        Bundle bundle = new Bundle();
        bundle.putString(PreferenceCategoryBuilder.BUNDLE_KEYATTR_KEY, key);
        bundle.putString(PreferenceCategoryBuilder.BUNDLE_TITLE_KEY, title);

        PreferenceCategory category = new PreferenceCategoryBuilder()
                .setBundle(bundle)
                .setContext(getContext())
                .build();

        assertEquals(key, category.getKey());
        assertEquals(title, category.getTitle());
    }
}
