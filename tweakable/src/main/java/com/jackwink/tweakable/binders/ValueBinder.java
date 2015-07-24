package com.jackwink.tweakable.binders;

import android.content.SharedPreferences;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Binds a value type to a given to a field.
 *
 * @param <T> Type of value that will be bound
 */
public interface ValueBinder<T> {

    void bindValue(SharedPreferences preferences, String key);

    Class<T> getType();

    T getValue();

    @SuppressWarnings({ "rawtypes" })
    enum DeclaredTypes {
        BOOL(new Class[] {Boolean.class, boolean.class }),
        INT(new Class[] {Integer.class, int.class }),
        FLOAT(new Class[] {Float.class, float.class }),
        STRING(new Class[] {String.class }),
        ACTION(new Class[] {Method.class });

        private Class[] mHandledTypes;

        DeclaredTypes(Class[] types) {
            mHandledTypes = types;
        }

        public boolean contains(Class cls) {
            for (Class clazz : mHandledTypes) {
                if (clazz.equals(cls)) {
                    return true;
                }
            }
            return false;
        }

        public ValueBinder getBinder(Object fieldOrMethod) {
            switch (this) {
                case BOOL:
                    return new BooleanValueBinder((Field) fieldOrMethod);
                case INT:
                    return new IntegerValueBinder((Field) fieldOrMethod);
                case FLOAT:
                    return new FloatValueBinder((Field) fieldOrMethod);
                case STRING:
                    return new StringValueBinder((Field) fieldOrMethod);
                case ACTION:
                    return new ActionBinder((Method) fieldOrMethod);
                default:
                    return null;
            }
        }
    }
}
