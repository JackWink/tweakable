package com.jackwink.tweakable.controls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

/**
 *
 */
public class SliderPreference extends DialogPreference {
    private static final String TAG = SliderPreference.class.getSimpleName();

    private static final int MAX_VALUE = 100;

    private SeekBar mSlider;
    private int mSelectedValue;


    private float mOriginalValue;
    private float mValue;
    private float mMinValue;
    private float mMaxValue;
    private float mRange;

    public SliderPreference(Context context) {
        super(context, null);
    }

    public void setMaxValue(float maxValue) {
        mMaxValue = maxValue;
        mRange = mMaxValue - mMinValue;
    }

    public void setMinValue(float minValue) {
        mMinValue = minValue;
        mRange = mMaxValue - mMinValue;
    }

    @Override
    protected void onSetInitialValue(final boolean restoreValue, final Object defaultValue) {
        setValue(restoreValue ? getPersistedFloat(0f) : (Float) defaultValue, false);
        mOriginalValue = mValue;
    }

    @Override
    protected Object onGetDefaultValue(final TypedArray a, final int index) {
        return a.getFloat(index, 0f);
    }

    @Override
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        mSlider = new SeekBar(getContext());
        mSlider.setMax(MAX_VALUE);
        mSlider.setProgress(mSelectedValue);
        mSlider.setOnSeekBarChangeListener(new ValueChangeListener());
        mSlider.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        final LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(mSlider);
        builder.setView(linearLayout);
    }

    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        if (positiveResult && shouldPersist()) {
            setSelectedValue(mSlider.getProgress(), true);
        } else {
            setValue(getPersistedFloat(mOriginalValue), false);
        }
    }

    private void setSelectedValue(int value, boolean persist) {
        mSelectedValue = value;
        mValue = mMinValue + ((((float)mSelectedValue /(float)MAX_VALUE)) * mRange);
        if (mValue > mMaxValue) {
            mValue = mMaxValue;
        }

        if (persist) {
            persistFloat(mValue);
        }
        updateSummary();
    }

    private void setValue(float value, boolean persist) {
        mValue = value;
        mSelectedValue = (int)(((mValue - mMinValue) * 100) / mRange);
        if (mSelectedValue > MAX_VALUE) {
            mSelectedValue = MAX_VALUE;
        }

        if (persist) {
            persistFloat(mValue);
        }
        updateSummary();
    }

    private void updateSummary() {
        this.setSummary(Float.toString(mValue));
    }

    private class ValueChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            SliderPreference.this.setSelectedValue(progress, false);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
