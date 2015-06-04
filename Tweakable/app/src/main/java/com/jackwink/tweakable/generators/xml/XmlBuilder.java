package com.jackwink.tweakable.generators.xml;


import org.xmlpull.v1.XmlSerializer;

/**
 * Eventually to be used to statically generate a settings file?
 */
public interface XmlBuilder {

    /**
     * Inserts itself and all its children into an XmlSerializer element.
     *
     * @return XML serializer after adding elements
     */
    XmlSerializer build(XmlSerializer serializer);
}
