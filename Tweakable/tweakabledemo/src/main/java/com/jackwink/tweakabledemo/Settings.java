package com.jackwink.tweakabledemo;

import com.jackwink.tweakable.annotations.TwkBoolean;

/**
 *
 */
public class Settings {
    private static final String SUBSCREEN = "And Subscreens?";
    private static final String SUBCATEGORY = "With Subcategories?";

    @TwkBoolean(title = "Some Feature", defaultsTo = false)
    public static boolean featureFlag;

    @TwkBoolean(category = "Categories are cool too!", title = "Some Feature 2",
            onSummary = "Some feature 2 enabled", offSummary = "Some feature 2 disabled",
            defaultsTo = false)
    public static boolean featureFlag2;

    @TwkBoolean(screen = SUBSCREEN, category = SUBCATEGORY, title = "Some Feature 3",
            onSummary = "Some feature 3 enabled", offSummary = "Some feature 3 disabled",
            defaultsTo = true)
    public static boolean featureFlag3;

    @TwkBoolean(screen = SUBSCREEN, category = SUBCATEGORY, defaultsTo = true,
                onSummary = "See how easy it is to add options?")
    public static boolean featureFlag4;

}
