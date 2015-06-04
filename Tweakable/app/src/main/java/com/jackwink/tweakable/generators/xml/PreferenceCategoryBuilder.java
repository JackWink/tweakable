package com.jackwink.tweakable.generators.xml;

import android.util.Log;

import com.jackwink.tweakable.exceptions.FailedToBuildXmlException;
import com.jackwink.tweakable.exceptions.NoPreferencesDefinedException;
import com.jackwink.tweakable.exceptions.TooManyPreferencesException;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class PreferenceCategoryBuilder implements XmlBuilder {
    private static final String TAG = PreferenceCategoryBuilder.class.getSimpleName();

    // TODO: investigate usefulness
    private static final int MAX_PREFERENCES = 16;

    private static final String ELEMENT_NAME = "PreferenceCategory";

    // default namespace of the attribute keys - android:{keyname}
    private static final String NAMESPACE = "android";

    // Title attribute is shown to the user as the category name.
    private static final String TITLE_ATTRIBUTE = "title";

    // we use the title as the key
    private static final String KEY_ATTRIBUTE = "key";

    private LinkedHashMap<String, String> mAttributeMap = new LinkedHashMap<>(2);

    private ArrayList<PreferenceBuilder> mPreferences = new ArrayList<>(MAX_PREFERENCES);

    public PreferenceCategoryBuilder() {}

    public PreferenceCategoryBuilder setTitle(String title) {
        mAttributeMap.put(TITLE_ATTRIBUTE, title);
        mAttributeMap.put(KEY_ATTRIBUTE, formatTitleToKey(title));
        return this;
    }

    /**
     * Sets the preferences for a given category.
     *
     * @param preferences
     * @return the updated PreferenceCategoryBuilder
     */
    public PreferenceCategoryBuilder setPreferences(PreferenceBuilder[] preferences) {
        if (preferences.length > MAX_PREFERENCES) {
            throw new TooManyPreferencesException("You cannot have more than " + MAX_PREFERENCES +
                    " defined in a category.  Either put it in a different category, or increase " +
                    " the limit defined in " + TAG);
        }
        mPreferences.addAll(Arrays.asList(preferences));
        return this;
    }

    /** {@link XmlBuilder#build(XmlSerializer)} */
    public XmlSerializer build(XmlSerializer serializer) {
        if (mPreferences.size() == 0) {
            throw new NoPreferencesDefinedException(
                    "There are no preferences defined for " + mAttributeMap.get(TITLE_ATTRIBUTE));
        }

        try {
            serializer.startTag(null, ELEMENT_NAME);
            for (Map.Entry<String, String> entry : mAttributeMap.entrySet()) {
                serializer.attribute(NAMESPACE, entry.getKey(), entry.getValue());
            }

            for (PreferenceBuilder builder : mPreferences) {
                builder.build(serializer);
            }
            serializer.endTag(null, ELEMENT_NAME);
        } catch (IOException error) {
            Log.e(TAG, "IO error when building XML.");
            throw new FailedToBuildXmlException("IO exception caused a build issue", error);
        }

        return serializer;
    }

    /**
     * Lowercases and strips spaces (replaces them with a '_' character) from a title.
     *
     * @param title Name of the category
     * @return normalized String to be used as the key
     */
    private static String formatTitleToKey(String title) {
        return title.toLowerCase().replace(' ', '_');
    }


}
