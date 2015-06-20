package com.jackwink.tweakabledemo;

import com.jackwink.tweakable.annotations.TwkBoolean;

/**
 *
 */
public class Settings {

    @TwkBoolean(category = "Boolean", title = "Lights?", onLabel = "ON!", offLabel = "OFF!",
            onSummary = "This light is on!", offSummary = "This light is off!", defaultsTo = true)
    public static boolean lightsOn;

    @TwkBoolean(category = "Boolean", title = "Other light", onLabel = "ON!", offLabel = "OFF!",
            onSummary = "This other light is on!", offSummary = "This other light is off!", defaultsTo = true)
    public static boolean otherLightsOn;
}
