package com.jackwink.tweakable;

import java.util.Collection;
import java.util.Map;

public interface PreferenceAnnotationProcessor {
    Collection<Map<String, Object>> getDeclaredScreens();
    Collection<Map<String, Object>> getDeclaredCategories();
    Collection<Map<String, Object>> getDeclaredPreferences();
}
