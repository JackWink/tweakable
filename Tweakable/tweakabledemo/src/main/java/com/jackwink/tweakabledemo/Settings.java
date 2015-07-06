package com.jackwink.tweakabledemo;

import android.widget.Toast;

import com.jackwink.tweakable.annotations.TwkAction;
import com.jackwink.tweakable.annotations.TwkBoolean;
import com.jackwink.tweakable.annotations.TwkFloat;
import com.jackwink.tweakable.annotations.TwkInteger;
import com.jackwink.tweakable.annotations.TwkString;

public class Settings {
    private static final String TAG = Settings.class.getSimpleName();

    private static final String SUBSCREEN = "Nested Screen";
    private static final String SUBCATEGORY = "Subscreen Feature Flags";
    private static final String FEATURE_FLAG_CATEGORY = "Feature Flags";
    private static final String STRINGS_CATEGORY = "String Options";
    private static final String INTS_CATEGORY = "Integer Options";
    private static final String FLOATS_CATEGORY = "Float Options";

    @TwkBoolean(category = FEATURE_FLAG_CATEGORY, title = "Feature Flag 1")
    public static boolean featureFlag = false;

    @TwkBoolean(category = FEATURE_FLAG_CATEGORY, title = "Feature Flag 2",
            onSummary = "Feature 2 enabled!", offSummary = "feature 2 disabled :(")
    public static boolean featureFlag2 = true;

    @TwkBoolean(screen = SUBSCREEN, category = SUBCATEGORY, title = "Feature Flag 3",
            onSummary = "Feature 3 enabled!", offSummary = "Feature 3 disabled :(")
    public static Boolean featureFlag3 = true;

    @TwkBoolean(screen = SUBSCREEN, category = SUBCATEGORY)
    public static boolean featureFlag4 = false;

    @TwkString(category = STRINGS_CATEGORY, title = "Selectable Strings",
            options = {"Thing 1", "Thing 2", "Thing 3"})
    public static String stringOptions1 = "Thing 2";

    @TwkString(category = STRINGS_CATEGORY, title = "Editable Strings")
    public static String stringOptions2 = "woooo!";

    @TwkInteger(category = INTS_CATEGORY, title = "Select Integer", summary = "Pick any number!")
    public static int intOptions1 = 20;

    @TwkInteger(category = INTS_CATEGORY, title = "Select Integer with range",
            summary = "Pick a number 10 - 100", maxValue = 100, minValue = 10)
    public static Integer intOptions2 = 30;


    @TwkFloat(category = FLOATS_CATEGORY, title = "Float Slider",
            summary = "Slides between 0 and 200", minValue = 0f, maxValue = 200f)
    public static float floatOption1 = 100f;

    @TwkAction(category = "Actions", title = "Log something")
    public static void doSomething() {
        Toast.makeText(App.getInstance(), "You did something!", Toast.LENGTH_LONG).show();
    }
}
