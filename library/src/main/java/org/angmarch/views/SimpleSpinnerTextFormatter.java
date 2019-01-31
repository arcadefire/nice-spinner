package org.angmarch.views;

import android.text.Spannable;
import android.text.SpannableString;

public class SimpleSpinnerTextFormatter implements SpinnerTextFormatter {

    @Override
    public final Spannable format(String text) {
        return new SpannableString(text);
    }

    @Override
    public Spannable format(Object item) {
        return new SpannableString(item.toString());
    }
}
