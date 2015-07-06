package com.jackwink.tweakable.binders;

import java.lang.reflect.Method;

/**
 *
 */
public class ActionBinder extends AbstractValueBinder<Method> {
    protected Method mMethod;

    public ActionBinder(Method method) {
        mMethod = method;
    }

    @Override
    public void bindValue(Method value) {
        try {
            mMethod.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<Method> getType() {
        return Method.class;
    }

    @Override
    public Method getValue() {
        return mMethod;
    }
}
