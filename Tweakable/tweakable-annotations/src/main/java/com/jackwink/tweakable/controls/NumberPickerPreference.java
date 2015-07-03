/*
 * Copyright (C) 2014-2015 Vanniktech - Niklas Baudy <http://vanniktech.de/Imprint>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jackwink.tweakable.controls;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {
    private static final String TAG = NumberPickerPreference.class.getSimpleName();

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;
    private static final boolean WRAP_SELECTOR_WHEEL = false;

    private int mSelectedValue;
    private int mMinValue = MIN_VALUE;
    private int mMaxValue = MAX_VALUE;
    private int mOriginalValue;
    private boolean mWrapSelectorWheel = WRAP_SELECTOR_WHEEL;
    private NumberPicker mNumberPicker;

    public NumberPickerPreference(final Context context) {
        super(context, null);
    }

    public void setMinValue(int value) {
        mMinValue = value;
    }

    public void setMaxValue(int value) {
        mMaxValue = value;
    }

    public void setWraps(boolean shouldWrap) {
        mWrapSelectorWheel = shouldWrap;
    }

    @Override
    protected void onSetInitialValue(final boolean restoreValue, final Object defaultValue) {
        setSelectedValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue, false);
        mOriginalValue = mSelectedValue;
    }

    @Override
    protected Object onGetDefaultValue(final TypedArray a, final int index) {
        return a.getInteger(index, 0);
    }

    @Override
    protected void onPrepareDialogBuilder(final Builder builder) {
        super.onPrepareDialogBuilder(builder);
        mNumberPicker = new NumberPicker(getContext());
        mNumberPicker.setMinValue(mMinValue);
        mNumberPicker.setMaxValue(mMaxValue);
        mNumberPicker.setValue(mSelectedValue);
        mNumberPicker.setWrapSelectorWheel(mWrapSelectorWheel);
        mNumberPicker.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < mNumberPicker.getChildCount(); i++) {
            View v = mNumberPicker.getChildAt(i);
            if (v instanceof EditText) {
                v.setOnKeyListener(new KeyListener());
            }
        }
        final LinearLayout linearLayout = new LinearLayout(this.getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(mNumberPicker);

        builder.setView(linearLayout);
    }

    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        if (positiveResult && shouldPersist()) {
            setSelectedValue(mNumberPicker.getValue(), true);
        } else {
            setSelectedValue(getPersistedInt(mOriginalValue), false);
        }
    }

    private void setSelectedValue(int value, boolean persist) {
        mSelectedValue = value;
        if (persist) {
            persistInt(mSelectedValue);
        }
        setSummary(Integer.toString(mSelectedValue));
    }

    private class KeyListener implements View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                String number = ((EditText) v).getText().toString();
                if (number.isEmpty()) {
                    setSelectedValue(mMinValue, false);
                } else {
                    int currentValue = Integer.valueOf(number);
                    if (currentValue >= mMinValue) {
                        setSelectedValue(Integer.valueOf(number), false);
                        mNumberPicker.setValue(mSelectedValue);
                    } else {
                        setSelectedValue(mMinValue, false);
                    }
                }
            }
            return false;
        }
    }

}
