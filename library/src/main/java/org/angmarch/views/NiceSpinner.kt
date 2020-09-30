package org.angmarch.views

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ListAdapter
import android.widget.ListPopupWindow
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import java.util.*

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
class NiceSpinner: AppCompatTextView {
    private var selectedIndex = 0
    private var arrowDrawable: Drawable? = null
    private var popupWindow: ListPopupWindow? = null
    private var adapter: NiceSpinnerBaseAdapter<*>? = null
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemSelectedListener: OnItemSelectedListener? = null
    var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener? = null
    var isArrowHidden = false
        private set
    private var tintColor = 0
    private var promptColor = 0
    private var backgroundSelector = 0
    private var arrowDrawableTint = 0
    private var displayHeight = 0
    private var parentVerticalOffset = 0
        private get() {
            if (field > 0) {
                return field
            }
            val locationOnScreen = IntArray(2)
            getLocationOnScreen(locationOnScreen)
            return locationOnScreen[VERTICAL_OFFSET].also { field = it }
        }
    var dropDownListPaddingBottom = 0

    @DrawableRes
    private var arrowDrawableResId = 0
    private var spinnerTextFormatter: SpinnerTextFormatter<*> = SimpleSpinnerTextFormatter()
    private var selectedTextFormatter: SpinnerTextFormatter<*>? = SimpleSpinnerTextFormatter()
    private var popUpTextAlignment: PopUpTextAlignment? = null
        private set
    private var arrowAnimator: ObjectAnimator? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putInt(SELECTED_INDEX, selectedIndex)
        bundle.putBoolean(IS_ARROW_HIDDEN, isArrowHidden)
        bundle.putInt(ARROW_DRAWABLE_RES_ID, arrowDrawableResId)
        if (popupWindow != null) {
            bundle.putBoolean(IS_POPUP_SHOWING, popupWindow!!.isShowing)
        }
        return bundle
    }

    override fun onRestoreInstanceState(savedState: Parcelable) {
        var savedState: Parcelable? = savedState
        if (savedState is Bundle) {
            val bundle = savedState
            selectedIndex = bundle.getInt(SELECTED_INDEX)
            if (adapter != null) {
                setTextInternal(selectedTextFormatter!!.format(adapter!!.getItemInDataset(selectedIndex)).toString())
                adapter!!.selectedIndex = selectedIndex
            }
            if (bundle.getBoolean(IS_POPUP_SHOWING)) {
                if (popupWindow != null) {
                    // Post the show request into the looper to avoid bad token exception
                    post { showDropDown() }
                }
            }
            isArrowHidden = bundle.getBoolean(IS_ARROW_HIDDEN, false)
            arrowDrawableResId = bundle.getInt(ARROW_DRAWABLE_RES_ID)
            savedState = bundle.getParcelable(INSTANCE_STATE)
        }
        super.onRestoreInstanceState(savedState)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val resources = resources
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NiceSpinner)
        val defaultPadding = resources.getDimensionPixelSize(R.dimen.one_and_a_half_grid_unit)
        gravity = Gravity.CENTER_VERTICAL or Gravity.START
        setPadding(resources.getDimensionPixelSize(R.dimen.three_grid_unit), defaultPadding, defaultPadding,
                defaultPadding)
        isClickable = true
        backgroundSelector = typedArray.getResourceId(R.styleable.NiceSpinner_backgroundSelector, R.drawable.selector)
        setBackgroundResource(backgroundSelector)
        tintColor = typedArray.getColor(R.styleable.NiceSpinner_textTint, getDefaultTextColor(context))
        setTextColor(tintColor)
        promptColor = typedArray.getColor(R.styleable.NiceSpinner_promptColor, getDefaultTextColor(context))
        popupWindow = ListPopupWindow(context)
        popupWindow!!.setOnItemClickListener { parent, view, position, id -> // The selected item is not displayed within the list, so when the selected position is equal to
            // the one of the currently selected item it gets shifted to the next item.
            var position = position
            if (position >= selectedIndex && position < adapter!!.count) {
                position++
            }
            selectedIndex = position
            if (onSpinnerItemSelectedListener != null) {
                onSpinnerItemSelectedListener!!.onItemSelected(this@NiceSpinner, view, position, id)
            }
            if (onItemClickListener != null) {
                onItemClickListener!!.onItemClick(parent, view, position, id)
            }
            if (onItemSelectedListener != null) {
                onItemSelectedListener!!.onItemSelected(parent, view, position, id)
            }
            adapter!!.selectedIndex = position
            setTextInternal(adapter!!.getItemInDataset(position))
            dismissDropDown()
        }
        popupWindow!!.isModal = true
        popupWindow!!.setOnDismissListener {
            if (!isArrowHidden) {
                animateArrow(false)
            }
        }
        isArrowHidden = typedArray.getBoolean(R.styleable.NiceSpinner_hideArrow, false)
        arrowDrawableTint = typedArray.getColor(R.styleable.NiceSpinner_arrowTint, getResources().getColor(android.R.color.black))
        arrowDrawableResId = typedArray.getResourceId(R.styleable.NiceSpinner_arrowDrawable, R.drawable.arrow)
        dropDownListPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.NiceSpinner_dropDownListPaddingBottom, 0)
        popUpTextAlignment = PopUpTextAlignment.fromId(
                typedArray.getInt(R.styleable.NiceSpinner_popupTextAlignment, PopUpTextAlignment.CENTER.ordinal)
        )
        val entries = typedArray.getTextArray(R.styleable.NiceSpinner_entries)
        if (entries != null) {
            attachDataSource(Arrays.asList(*entries))
        }
        typedArray.recycle()
        measureDisplayHeight()
    }

    private fun measureDisplayHeight() {
        displayHeight = context.resources.displayMetrics.heightPixels
    }

    override fun onDetachedFromWindow() {
        if (arrowAnimator != null) {
            arrowAnimator!!.cancel()
        }
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            onVisibilityChanged(this, visibility)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        arrowDrawable = initArrowDrawable(arrowDrawableTint)
        setArrowDrawableOrHide(arrowDrawable)
    }

    private fun initArrowDrawable(drawableTint: Int): Drawable? {
        if (arrowDrawableResId == 0) return null
        var drawable = ContextCompat.getDrawable(context, arrowDrawableResId)
        if (drawable != null) {
            // Gets a copy of this drawable as this is going to be mutated by the animator
            drawable = DrawableCompat.wrap(drawable).mutate()
            if (drawableTint != Int.MAX_VALUE && drawableTint != 0) {
                DrawableCompat.setTint(drawable, drawableTint)
            }
        }
        return drawable
    }

    private fun setArrowDrawableOrHide(drawable: Drawable?) {
        if (!isArrowHidden && drawable != null) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun getDefaultTextColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme
                .resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        val typedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(android.R.attr.textColorPrimary))
        val defaultTextColor = typedArray.getColor(0, Color.BLACK)
        typedArray.recycle()
        return defaultTextColor
    }

    fun getItemAtPosition(position: Int): Any {
        return adapter!!.getItemInDataset(position)
    }

    val selectedItem: Any
        get() = adapter!!.getItemInDataset(selectedIndex)

    fun getSelectedIndex(): Int {
        return selectedIndex
    }

    fun setArrowDrawable(@DrawableRes @ColorRes drawableId: Int) {
        arrowDrawableResId = drawableId
        arrowDrawable = initArrowDrawable(R.drawable.arrow)
        setArrowDrawableOrHide(arrowDrawable)
    }

    fun setArrowDrawable(drawable: Drawable?) {
        arrowDrawable = drawable
        setArrowDrawableOrHide(arrowDrawable)
    }

    private fun<T> setTextInternal(item: T) {
        if (selectedIndex == 0) {
            setTextColor(promptColor)
        } else {
            setTextColor(tintColor)
        }
        text = if (selectedTextFormatter != null) {
            selectedTextFormatter!!.format(item)
        } else {
            item.toString()
        }
    }

    /**
     * Set the default spinner item using its index
     *
     * @param position the item's position
     */
    fun setSelectedIndex(position: Int) {
        if (adapter != null) {
            if (position >= 0 && position <= adapter!!.count) {
                adapter!!.selectedIndex = position
                selectedIndex = position
                setTextInternal(selectedTextFormatter!!.format(adapter!!.getItemInDataset(position)).toString())
            } else {
                throw IllegalArgumentException("Position must be lower than adapter count!")
            }
        }
    }

    @Deprecated("use setOnSpinnerItemSelectedListener instead.")
    fun addOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    @Deprecated("use setOnSpinnerItemSelectedListener instead.")
    fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener?) {
        this.onItemSelectedListener = onItemSelectedListener
    }

    fun <T> attachDataSource(list: List<T>) {
        adapter = popUpTextAlignment?.let { NiceSpinnerAdapter(context, list, tintColor, backgroundSelector, spinnerTextFormatter, it) }
        setAdapterInternal(adapter)
    }

    fun setAdapter(adapter: ListAdapter?) {
        this.adapter = adapter?.let {
            NiceSpinnerAdapterWrapper(context, it, tintColor, backgroundSelector,
                spinnerTextFormatter, popUpTextAlignment)
        }
        setAdapterInternal(this.adapter)
    }

    private fun <T> setAdapterInternal(adapter: NiceSpinnerBaseAdapter<T>?) {
        if (adapter!!.count >= 0) {
            // If the adapter needs to be set again, ensure to reset the selected index as well
            selectedIndex = 0
            popupWindow!!.setAdapter(adapter)
            setTextInternal(adapter.getItemInDataset(selectedIndex))
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled && event.action == MotionEvent.ACTION_UP) {
            if (!popupWindow!!.isShowing && adapter!!.count > 0) {
                showDropDown()
            } else {
                dismissDropDown()
            }
        }
        return super.onTouchEvent(event)
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun animateArrow(shouldRotateUp: Boolean) {
        val start = if (shouldRotateUp) 0 else MAX_LEVEL
        val end = if (shouldRotateUp) MAX_LEVEL else 0
        arrowAnimator = ObjectAnimator.ofInt(arrowDrawable, "level", start, end)
//        arrowAnimator.setInterpolator(LinearOutSlowInInterpolator())
//        arrowAnimator.start()
    }

    fun dismissDropDown() {
        if (!isArrowHidden) {
            animateArrow(false)
        }
        popupWindow!!.dismiss()
    }

    fun showDropDown() {
        if (!isArrowHidden) {
            animateArrow(true)
        }
        popupWindow!!.anchorView = this
        popupWindow!!.show()
        val listView = popupWindow!!.listView
        if (listView != null) {
            listView.isVerticalScrollBarEnabled = false
            listView.isHorizontalScrollBarEnabled = false
            listView.isVerticalFadingEdgeEnabled = false
            listView.isHorizontalFadingEdgeEnabled = false
        }
    }

    private val popUpHeight: Int
        private get() = Math.max(verticalSpaceBelow(), verticalSpaceAbove())

    private fun verticalSpaceAbove(): Int {
        return parentVerticalOffset
    }

    private fun verticalSpaceBelow(): Int {
        return displayHeight - parentVerticalOffset - measuredHeight
    }

    fun setTintColor(@ColorRes resId: Int) {
        if (arrowDrawable != null && !isArrowHidden) {
            DrawableCompat.setTint(arrowDrawable!!, ContextCompat.getColor(context, resId))
        }
    }

    fun setArrowTintColor(resolvedColor: Int) {
        if (arrowDrawable != null && !isArrowHidden) {
            DrawableCompat.setTint(arrowDrawable!!, resolvedColor)
        }
    }

    fun hideArrow() {
        isArrowHidden = true
        setArrowDrawableOrHide(arrowDrawable)
    }

    fun showArrow() {
        isArrowHidden = false
        setArrowDrawableOrHide(arrowDrawable)
    }

    fun setSpinnerTextFormatter(spinnerTextFormatter: SpinnerTextFormatter<*>) {
        this.spinnerTextFormatter = spinnerTextFormatter
    }

    fun setSelectedTextFormatter(textFormatter: SpinnerTextFormatter<*>?) {
        selectedTextFormatter = textFormatter
    }

    fun performItemClick(position: Int, showDropdown: Boolean) {
        if (showDropdown) showDropDown()
        setSelectedIndex(position)
    }

    /**
     * only applicable when popup is shown .
     * @param view
     * @param position
     * @param id
     */
    fun performItemClick(view: View?, position: Int, id: Int) {
        showDropDown()
        val listView = popupWindow!!.listView
        listView?.performItemClick(view, position, id.toLong())
    }

    companion object {
        private const val MAX_LEVEL = 10000
        private const val VERTICAL_OFFSET = 1
        private const val INSTANCE_STATE = "instance_state"
        private const val SELECTED_INDEX = "selected_index"
        private const val IS_POPUP_SHOWING = "is_popup_showing"
        private const val IS_ARROW_HIDDEN = "is_arrow_hidden"
        private const val ARROW_DRAWABLE_RES_ID = "arrow_drawable_res_id"
    }
}