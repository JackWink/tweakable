package com.jackwink.tweakable.generators.xml;

import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedHashMap;

/**
 *
 */
public class PreferenceBuilder implements XmlBuilder {
    private static final String TAG = PreferenceBuilder.class.getSimpleName();

    private static LinkedHashMap<String, String> mTypeToElementMap = new LinkedHashMap<>();
    static {
        mTypeToElementMap.put(int.class.getSimpleName(), "");
    }

    /** {@link XmlBuilder#build(XmlSerializer)} */
    @Override
    public XmlSerializer build(XmlSerializer serializer) {
        return serializer;
    }
}
