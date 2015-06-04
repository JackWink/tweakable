package com.jackwink.tweakable.generators;


import org.xmlpull.v1.XmlSerializer;

public interface XmlBuilder {

    /**
     * Inserts itself and all its children into an XmlSerializer element.
     *
     * @return XML serializer after adding elements
     */
    XmlSerializer build(XmlSerializer serializer);
}
