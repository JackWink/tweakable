package com.jackwink.tweakabledemo;

import com.jackwink.tweakable.annotations.TwkBoolean;

/**
 *
 */
public class Settings {

    @TwkBoolean(screen = "light settings", title = "Lights?", onLabel = "ON!", offLabel = "OFF!",
            defaultsTo = true)
    public static boolean lightsOn;

    @TwkBoolean(screen = "light settings", category = "Boolean 2", title = "Other light", onLabel = "ON!", offLabel = "OFF!",
            onSummary = "This other light is on!", offSummary = "This other light is off!", defaultsTo = false)
    public static boolean otherLightsOn;
}
