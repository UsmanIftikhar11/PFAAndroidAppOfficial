package com.pfa.pfaapp.customviews;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

public class CNICEditText extends PFAEditText {

    public CNICEditText(Context context) {
        super(context);
    }
    public CNICEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        setSelection(getText().length());
    }
}
