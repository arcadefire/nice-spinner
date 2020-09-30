package org.angmarch.views

import android.view.View

interface OnSpinnerItemSelectedListener {
    fun onItemSelected(parent: NiceSpinner?, view: View?, position: Int, id: Long)
}