package org.angmarch.views;

import android.text.Spannable;

public interface SpinnerTextFormatter {

    Spannable formatSelectedText(String text);

    Spannable formatEntryText(String text);
}