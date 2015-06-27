package com.jackwink.tweakabledemo;

import com.jackwink.tweakable.annotations.TwkBoolean;
import com.jackwink.tweakable.annotations.TwkString;

/**
 *
 */
public class Settings {
    private static final String SUBSCREEN = "Nested Screen";
    private static final String SUBCATEGORY = "Subscreen Feature Flags";
    private static final String FEATURE_FLAG_CATEGORY = "Feature Flags";
    private static final String STRINGS_CATEGORY = "String Options";

    @TwkBoolean(category = FEATURE_FLAG_CATEGORY, title = "Feature Flag 1", defaultsTo = true)
    public static boolean featureFlag;

    @TwkBoolean(category = FEATURE_FLAG_CATEGORY, title = "Feature Flag 2",
            onSummary = "Feature 2 enabled!", offSummary = "feature 2 disabled :(")
    public static boolean featureFlag2;

    @TwkBoolean(screen = SUBSCREEN, category = SUBCATEGORY, title = "Feature Flag 3",
            onSummary = "Feature 3 enabled!", offSummary = "Feature 3 disabled :(")
    public static Boolean featureFlag3;

    @TwkBoolean(screen = SUBSCREEN, category = SUBCATEGORY, defaultsTo = true)
    public static boolean featureFlag4;

    @TwkString(category = STRINGS_CATEGORY, title = "Selectable Strings",
            options = {"Thing 1", "Thing 2", "Thing 3"}, defaultsTo = "Thing 1")
    public static String stringOptions1;

    @TwkString(category = STRINGS_CATEGORY, title = "Editable Strings", defaultsTo = "Edit me!")
    public static String stringOptions2;

}
