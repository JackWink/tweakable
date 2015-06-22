package com.jackwink.tweakable.generators.java;

import android.os.Bundle;

import com.jackwink.tweakable.controls.TweaksFragment;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 */
public class GeneratedProcessedAnnotations implements TweaksFragment.PreferenceAnnotationProcessor {
    private static Set<Bundle> mScreens = new LinkedHashSet<>();
    private static Set<Bundle> mCategories = new LinkedHashSet<>();
    private static Set<Bundle> mPreferences = new LinkedHashSet<>();


    static {
        Bundle tmp = new Bundle();
        mScreens.add(tmp);
    }

    @Override
    public Collection<Bundle> getDeclaredScreens() {
        return mScreens;
    }

    @Override
    public Collection<Bundle> getDeclaredCategories() {
        return mCategories;
    }

    @Override
    public Collection<Bundle> getDeclaredPreferences() {
        return mPreferences;
    }
}
