package com.jackwink.tweakable.types;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 *
 */
public interface PreferenceEntry {

    String getKey();

    String getTitle();

    String getSummary();

    Bundle toBundle();

    String getCategory();

    @Nullable
    PreferenceEntry[] getChildren();
}
