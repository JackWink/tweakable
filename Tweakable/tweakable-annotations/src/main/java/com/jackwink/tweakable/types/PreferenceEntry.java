package com.jackwink.tweakable.types;

import android.os.Bundle;

/**
 *
 */
public interface PreferenceEntry {

    String getKey();

    String getTitle();

    String getSummary();

    String getScreen();

    Bundle toBundle();

    String getCategory();

    PreferenceEntry[] getChildren();
}
