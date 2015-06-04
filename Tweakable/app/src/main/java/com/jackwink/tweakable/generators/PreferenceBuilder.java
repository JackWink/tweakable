package com.jackwink.tweakable.generators;

import org.xmlpull.v1.XmlSerializer;

/**
 *
 */
public class PreferenceBuilder implements XmlBuilder {
    private static final String TAG = PreferenceBuilder.class.getSimpleName();
    
    /** {@link XmlBuilder#build(XmlSerializer)} */
    @Override
    public XmlSerializer build(XmlSerializer serializer) {
        return serializer;
    }
}
