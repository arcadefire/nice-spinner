package org.angmarch.views

import android.text.Spannable

interface SpinnerTextFormatter<T> {
    fun<T> format(item: T?): Spannable?
}