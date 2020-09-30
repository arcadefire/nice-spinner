package org.angmarch.views

import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

/*
* Copyright (C) 2015 Angelo Marchesin.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
abstract class NiceSpinnerBaseAdapter<T> internal constructor(
        context: Context?,
        private val textColor: Int,
        private val backgroundSelector: Int,
        private val spinnerTextFormatter: SpinnerTextFormatter<*>?,
        private val horizontalAlignment: PopUpTextAlignment?
) : BaseAdapter() {
    var selectedIndex = 0
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val context = parent.context
        val textView: TextView
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.spinner_list_item, null)
            textView = convertView.findViewById(R.id.text_view_spinner)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textView.background = ContextCompat.getDrawable(context, backgroundSelector)
            }
            convertView.tag = ViewHolder(textView)
        } else {
            textView = (convertView.tag as ViewHolder).textView
        }
        if (spinnerTextFormatter != null) {
            textView.text = spinnerTextFormatter?.format(getItem(position))
        }
        textView.setTextColor(textColor)
        setTextHorizontalAlignment(textView)
        return convertView!!
    }

    private fun setTextHorizontalAlignment(textView: TextView) {
        when (horizontalAlignment) {
            PopUpTextAlignment.START -> textView.gravity = Gravity.START
            PopUpTextAlignment.END -> textView.gravity = Gravity.END
            PopUpTextAlignment.CENTER -> textView.gravity = Gravity.CENTER_HORIZONTAL
        }
    }

    abstract fun getItemInDataset(position: Int): Any
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    abstract override fun getItem(position: Int): Any
    abstract override fun getCount(): Int
    internal class ViewHolder(var textView: TextView)
}