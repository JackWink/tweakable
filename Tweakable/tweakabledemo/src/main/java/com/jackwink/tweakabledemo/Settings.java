package com.jackwink.tweakabledemo;

import com.jackwink.tweakable.annotations.TwkBoolean;

/**
 *
 */
public class Settings {

    @TwkBoolean(screen = "Light settingss", title = "Lights?", defaultsTo = true)
    public static boolean lightsOn;

    @TwkBoolean(title = "Some Feature", onSummary = "Some feature enabled",
            offSummary = "Some feature disabled", defaultsTo = false)
    public static boolean featureFlag;

    @TwkBoolean(category = "Something Cool", title = "Some Feature 2",
            onSummary = "Some feature 2 enabled", offSummary = "Some feature 2 disabled",
            defaultsTo = false)
    public static boolean featureFlag2;

    @TwkBoolean(screen = "Light settingss", title = "Some Feature 3",
            onSummary = "Some feature 3 enabled", offSummary = "Some feature 3 disabled",
            defaultsTo = true)
    public static boolean featureFlag3;

}
