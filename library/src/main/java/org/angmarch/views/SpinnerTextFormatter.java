package org.angmarch.views;

import android.text.Spannable;

public interface SpinnerTextFormatter<T> {

    Spannable formatSelectedText(T text);

    Spannable formatEntryText(T text);
}
