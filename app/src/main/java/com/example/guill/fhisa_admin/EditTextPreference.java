package com.example.guill.fhisa_admin;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by guill on 12/12/2017.
 */

public class EditTextPreference extends android.preference.EditTextPreference{
    public EditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextPreference(Context context) {
        super(context);
    }

    @Override
    public CharSequence getSummary() {
        if ( super.getSummary() == null ) { return null; }
        String summary = super.getSummary().toString();
        return String.format(summary, getText());
    }
}
