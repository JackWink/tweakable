package com.jackwink.tweakable.controls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.DialogPreference;

/**
 *
 */
public class ActionPreference extends DialogPreference {
    private int mCalled;

    public ActionPreference(Context context) {
        super(context, null);
    }

    @Override
    protected void onSetInitialValue(final boolean restoreValue, final Object defaultValue) {
        mCalled = getPersistedInt(0);
    }

    @Override
    protected Object onGetDefaultValue(final TypedArray a, final int index) {
        return a.getInteger(index, 0);
    }

    @Override
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        builder.setTitle(getTitle());
        builder.setMessage("Are you sure you want to " + getTitle().toString().toLowerCase() + "?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ++mCalled;
                // We just need to cause a value to change on this key for the method to be called
                // {@see com.jackwink.tweakable.Tweakable}
                persistInt(mCalled);
            }
        });
    }
}
