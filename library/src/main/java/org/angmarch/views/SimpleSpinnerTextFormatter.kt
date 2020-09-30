package org.angmarch.views

import android.text.Spannable
import android.text.SpannableString

open class SimpleSpinnerTextFormatter : SpinnerTextFormatter<Any?> {
    override fun <T> format(item: T?): Spannable? {
        return SpannableString(item.toString())
    }

}