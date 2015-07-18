package com.jackwink.tweakable.annotations.processor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Android-like log syntax
 */
public class Log {
    private static Log instance = null;

    private Messager mMessager;

    public static void init(Messager messager) {
        instance = new Log(messager);
    }

    public static boolean isInitialized() {
        return instance == null;
    }

    public static void i(String message) {
        print(Diagnostic.Kind.NOTE, message);
    }

    public static void d(String message) {
        print(Diagnostic.Kind.OTHER, message);
    }

    public static void e(String message) {
        print(Diagnostic.Kind.ERROR, message);
    }

    public static void i(String message, Element element) {
        print(Diagnostic.Kind.NOTE, message, element);
    }

    public static void d(String message, Element element) {
        print(Diagnostic.Kind.OTHER, message, element);
    }

    public static void e(String message, Element element) {
        print(Diagnostic.Kind.ERROR, message, element);
    }

    private static void print(Diagnostic.Kind kind, String message) {
        instance.mMessager.printMessage(kind, message);
    }

    private static void print(Diagnostic.Kind kind, String message, Element element) {
        instance.mMessager.printMessage(kind, message, element);
    }

    private Log(Messager messager) {
        mMessager = messager;
    }

}
