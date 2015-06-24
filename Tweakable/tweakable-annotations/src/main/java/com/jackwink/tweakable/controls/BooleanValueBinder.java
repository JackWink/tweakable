package com.jackwink.tweakable.controls;

/**
 * Binds a boolean value from shared preferences to a static field
 */
public class BooleanValueBinder extends AbstractValueBinder {

    @Override
    public void bindValue() {
        try {
            mField.setBoolean(null, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
