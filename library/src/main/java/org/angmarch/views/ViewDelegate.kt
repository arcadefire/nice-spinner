package org.angmarch.views

import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

interface ViewDelegate<T> {
    fun getView(position: Int, item: T, convertView: View?, parent: ViewGroup): View
}

internal class DefaultViewDelegate<T>(
        private val context: Context,
        private val spinnerTextFormatter: SpinnerTextFormatter<Any>,
        private val textColor: Int,
        private val backgroundSelector: Int,
        private val horizontalAlignment: PopUpTextAlignment
) : ViewDelegate<T> {

    private var innerLayoutResId = R.layout.spinner_list_item
    private var innerTextViewResId = R.id.text_view_spinner

    override fun getView(position: Int, item: T, convertView: View?, parent: ViewGroup): View {
        val rootView: View = convertView ?: View.inflate(context, innerLayoutResId, null)
        val context = parent.context
        val textView: TextView

        if (convertView == null) {
            textView = rootView.findViewById(innerTextViewResId)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textView.background = ContextCompat.getDrawable(context, backgroundSelector)
            }
            rootView.tag = ViewHolder(textView)
        } else {
            textView = (convertView.tag as ViewHolder).textView
        }

        textView.text = spinnerTextFormatter.format(item)
        textView.setTextColor(textColor)

        setTextHorizontalAlignment(textView)

        return rootView
    }

    private fun setTextHorizontalAlignment(textView: TextView) {
        when (horizontalAlignment) {
            PopUpTextAlignment.START -> textView.gravity = Gravity.START
            PopUpTextAlignment.END -> textView.gravity = Gravity.END
            PopUpTextAlignment.CENTER -> textView.gravity = Gravity.CENTER_HORIZONTAL
        }
    }

    internal data class ViewHolder(val textView: TextView)
}