package org.angmarch.views;

import android.text.Spannable;
import android.text.SpannableString;

public class SimpleSpinnerTextFormatter<Object> implements SpinnerTextFormatter<Object> {

    @Override
    public Spannable formatSelectedText(Object text) {
        return new SpannableString(text.toString());
    }

    @Override
    public Spannable formatEntryText(Object text) {
        return new SpannableString(text.toString());
    }
}
