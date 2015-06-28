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
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;
    private static final boolean WRAP_SELECTOR_WHEEL = false;

    private int mSelectedValue;
    private int mMinValue = MIN_VALUE;
    private int mMaxValue = MAX_VALUE;
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
        mSelectedValue = restoreValue ? this.getPersistedInt(0) : (Integer) defaultValue;
        this.updateSummary();
    }

    @Override
    protected Object onGetDefaultValue(final TypedArray a, final int index) {
        return a.getInteger(index, 0);
    }

    @Override
    protected void onPrepareDialogBuilder(final Builder builder) {
        super.onPrepareDialogBuilder(builder);

        mNumberPicker = new NumberPicker(this.getContext());
        mNumberPicker.setMinValue(mMinValue);
        mNumberPicker.setMaxValue(mMaxValue);
        mNumberPicker.setValue(mSelectedValue);
        mNumberPicker.setWrapSelectorWheel(mWrapSelectorWheel);
        mNumberPicker.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        final LinearLayout linearLayout = new LinearLayout(this.getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(mNumberPicker);

        builder.setView(linearLayout);
    }

    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        if (positiveResult && this.shouldPersist()) {
            mSelectedValue = mNumberPicker.getValue();
            this.persistInt(mSelectedValue);
            this.updateSummary();
        }
    }

    private void updateSummary() {
        this.setSummary(Integer.toString(mSelectedValue));
    }
}
