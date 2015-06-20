package com.jackwink.tweakable.types;

import android.os.Bundle;

/**
 *
 */
public interface PreferenceEntry {

    String getKey();

    String getTitle();

    String getSummary();

    Bundle toBundle();

    String getCategory();

    PreferenceEntry[] getChildren();
}
