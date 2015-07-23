package com.jackwink.tweakable.binders;

import android.content.SharedPreferences;

import java.lang.reflect.Method;

/**
 *
 */
public class ActionBinder extends AbstractValueBinder<Method> {
    public static final Class[] DECLARED_TYPES = {Method.class };

    protected Method mMethod;

    public ActionBinder(Method method) {
        mMethod = method;
    }

    @Override
    public Class<Method> getType() {
        return Method.class;
    }

    @Override
    public void bindValue(SharedPreferences preferences, String key) {
        try {
            mMethod.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Method getValue() {
        return mMethod;
    }
}
