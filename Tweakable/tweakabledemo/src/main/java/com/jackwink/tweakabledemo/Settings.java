package com.jackwink.tweakabledemo;

import com.jackwink.tweakable.annotations.TwkBoolean;
import com.jackwink.tweakable.annotations.TwkString;

/**
 *
 */
public class Settings {
    private static final String SUBSCREEN = "And Subscreens!";
    private static final String SUBCATEGORY = "With Subcategories!";

    private static final String STR_OPTION_1 = "Apples";
    private static final String STR_OPTION_2 = "Pairs";
    private static final String STR_OPTION_3 = "Grapefruits";


    @TwkBoolean(title = "Some Feature", defaultsTo = true)
    public static boolean featureFlag;

    @TwkBoolean(category = "Categories are cool too!", title = "Some Feature 2",
            onSummary = "Some feature 2 enabled", offSummary = "Some feature 2 disabled")
    public static boolean featureFlag2;

    @TwkBoolean(screen = SUBSCREEN, category = SUBCATEGORY, title = "Some Feature 3",
            onSummary = "Some feature 3 enabled", offSummary = "Some feature 3 disabled")
    public static Boolean featureFlag3;

    @TwkBoolean(screen = SUBSCREEN, defaultsTo = true,
            onSummary = "See how easy it is to add options?")
    public static boolean featureFlag4;

    @TwkString(screen = SUBSCREEN, category = SUBCATEGORY, title = "Selectable Things",
            options = {STR_OPTION_1, STR_OPTION_2, STR_OPTION_3}, defaultsTo = STR_OPTION_1)
    public static String stringOptions;

}
