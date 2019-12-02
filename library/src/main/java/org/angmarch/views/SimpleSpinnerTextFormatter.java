package org.angmarch.views;

import android.text.Spannable;
import android.text.SpannableString;

public class SimpleSpinnerTextFormatter implements SpinnerTextFormatter {

    @Override
    public Spannable formatSelectedText(String text) {
        return new SpannableString(text);
    }

    @Override
    public Spannable formatEntryText(String text) {
        return new SpannableString(text);
    }
}
