package org.angmarch.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.List;

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
@SuppressWarnings("unused")
public class NiceSpinner extends AppCompatTextView {

    private static final int MAX_LEVEL = 10000;
    private static final int DEFAULT_ELEVATION = 16;
    private static final String INSTANCE_STATE = "instance_state";
    private static final String SELECTED_INDEX = "selected_index";
    private static final String IS_POPUP_SHOWING = "is_popup_showing";
    private static final String IS_ARROW_HIDDEN = "is_arrow_hidden";
    private static final String ARROW_DRAWABLE_RES_ID = "arrow_drawable_res_id";

    private int selectedIndex;
    private Drawable arrowDrawable;
    private PopupWindow popupWindow;
    private ListView listView;
    private NiceSpinnerBaseAdapter adapter;
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemSelectedListener onItemSelectedListener;
    private boolean isArrowHidden;
    private int textColor;
    private int backgroundSelector;
    private int arrowDrawableTint;
    private int dropDownListPaddingBottom;
    private @DrawableRes int arrowDrawableResId;

    private SpinnerTextFormatter spinnerTextFormatter = new SimpleSpinnerTextFormatter();

    public NiceSpinner(Context context) {
        super(context);
        init(context, null);
    }

    public NiceSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NiceSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(SELECTED_INDEX, selectedIndex);
        bundle.putBoolean(IS_ARROW_HIDDEN, isArrowHidden);
        bundle.putInt(ARROW_DRAWABLE_RES_ID, arrowDrawableResId);
        if (popupWindow != null) {
            bundle.putBoolean(IS_POPUP_SHOWING, popupWindow.isShowing());
        }
        return bundle;
    }

    @Override public void onRestoreInstanceState(Parcelable savedState) {
        if (savedState instanceof Bundle) {
            Bundle bundle = (Bundle) savedState;
            selectedIndex = bundle.getInt(SELECTED_INDEX);

            if (adapter != null) {
                setText(adapter.getItemInDataset(selectedIndex).toString());
                adapter.setSelectedIndex(selectedIndex);
            }

            if (bundle.getBoolean(IS_POPUP_SHOWING)) {
                if (popupWindow != null) {
                    // Post the show request into the looper to avoid bad token exception
                    post(new Runnable() {
                        @Override
                        public void run() {
                            showDropDown();
                        }
                    });
                }
            }

            isArrowHidden = bundle.getBoolean(IS_ARROW_HIDDEN, false);
            arrowDrawableResId = bundle.getInt(ARROW_DRAWABLE_RES_ID);
            savedState = bundle.getParcelable(INSTANCE_STATE);
        }
        super.onRestoreInstanceState(savedState);
    }

    private void init(Context context, AttributeSet attrs) {
        Resources resources = getResources();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NiceSpinner);
        int defaultPadding = resources.getDimensionPixelSize(R.dimen.one_and_a_half_grid_unit);

        setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        setPadding(resources.getDimensionPixelSize(R.dimen.three_grid_unit), defaultPadding, defaultPadding,
                defaultPadding);
        setClickable(true);

        backgroundSelector = typedArray.getResourceId(R.styleable.NiceSpinner_backgroundSelector, R.drawable.selector);
        setBackgroundResource(backgroundSelector);
        textColor = typedArray.getColor(R.styleable.NiceSpinner_textTint, getDefaultTextColor(context));
        setTextColor(textColor);

        listView = new ListView(context);
        // Set the spinner's id into the listview to make it pretend to be the right parent in
        // onItemClick
        listView.setId(getId());
        listView.setDivider(null);
        listView.setItemsCanFocus(true);
        //hide vertical and horizontal scrollbars
        listView.setVerticalScrollBarEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= selectedIndex && position < adapter.getCount()) {
                    position++;
                }

                // Need to set selected index before calling listeners or getSelectedIndex() can be
                // reported incorrectly due to race conditions.
                selectedIndex = position;

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(parent, view, position, id);
                }

                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(parent, view, position, id);
                }

                adapter.setSelectedIndex(position);
                setText(adapter.getItemInDataset(position).toString());
                dismissDropDown();
            }
        });

        popupWindow = new PopupWindow(context);
        popupWindow.setContentView(listView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(DEFAULT_ELEVATION);
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.spinner_drawable));
        } else {
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.drop_down_shadow));
        }

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!isArrowHidden) {
                    animateArrow(false);
                }
            }
        });

        arrowDrawableTint = typedArray.getColor(R.styleable.NiceSpinner_arrowTint, Integer.MAX_VALUE);
        arrowDrawableResId = typedArray.getResourceId(R.styleable.NiceSpinner_arrowDrawable, R.drawable.arrow);
        dropDownListPaddingBottom =
                typedArray.getDimensionPixelSize(R.styleable.NiceSpinner_dropDownListPaddingBottom, 0);
        typedArray.recycle();
    }

    @Override protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        arrowDrawable = initArrowDrawable(arrowDrawableTint);
        setArrowDrawableOrHide(arrowDrawable);
    }

    private Drawable initArrowDrawable(int drawableTint) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), arrowDrawableResId);
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            if (drawableTint != Integer.MAX_VALUE && drawableTint != 0) {
                DrawableCompat.setTint(drawable, drawableTint);
            }
        }
        return drawable;
    }

    private void setArrowDrawableOrHide(Drawable drawable) {
        if (!isArrowHidden && drawable != null) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    private int getDefaultTextColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme()
                .resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray typedArray = context.obtainStyledAttributes(typedValue.data,
                new int[]{android.R.attr.textColorPrimary});
        int defaultTextColor = typedArray.getColor(0, Color.BLACK);
        typedArray.recycle();
        return defaultTextColor;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setArrowDrawable(@DrawableRes @ColorRes int drawableId) {
        arrowDrawableResId = drawableId;
        arrowDrawable = initArrowDrawable(R.drawable.arrow);
        setArrowDrawableOrHide(arrowDrawable);
    }

    public void setArrowDrawable(Drawable drawable) {
        arrowDrawable = drawable;
        setArrowDrawableOrHide(arrowDrawable);
    }

    /**
     * Set the default spinner item using its index
     *
     * @param position the item's position
     */
    public void setSelectedIndex(int position) {
        if (adapter != null) {
            if (position >= 0 && position <= adapter.getCount()) {
                adapter.setSelectedIndex(position);
                selectedIndex = position;
                setText(adapter.getItemInDataset(position).toString());
            } else {
                throw new IllegalArgumentException("Position must be lower than adapter count!");
            }
        }
    }

    public void addOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public <T> void attachDataSource(List<T> list) {
        adapter = new NiceSpinnerAdapter<>(getContext(), list, textColor, backgroundSelector,
                spinnerTextFormatter);
        setAdapterInternal(adapter);
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = new NiceSpinnerAdapterWrapper(getContext(), adapter, textColor, backgroundSelector,
                spinnerTextFormatter);
        setAdapterInternal(this.adapter);
    }

    private void setAdapterInternal(NiceSpinnerBaseAdapter adapter) {
        // If the adapter needs to be settled again, ensure to reset the selected index as well
        selectedIndex = 0;
        listView.setAdapter(adapter);
        setText(adapter.getItemInDataset(selectedIndex).toString());
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        listView.measure(widthMeasureSpec, heightMeasureSpec);
        popupWindow.setWidth(View.MeasureSpec.getSize(widthMeasureSpec));
        popupWindow.setHeight(listView.getMeasuredHeight() - getMeasuredHeight() - dropDownListPaddingBottom);
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled() && event.getAction() == MotionEvent.ACTION_UP) {
            if (!popupWindow.isShowing()) {
                showDropDown();
            } else {
                dismissDropDown();
            }
        }
        return super.onTouchEvent(event);
    }

    private void animateArrow(boolean shouldRotateUp) {
        int start = shouldRotateUp ? 0 : MAX_LEVEL;
        int end = shouldRotateUp ? MAX_LEVEL : 0;
        ObjectAnimator animator = ObjectAnimator.ofInt(arrowDrawable, "level", start, end);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.start();
    }

    public void dismissDropDown() {
        if (!isArrowHidden) {
            animateArrow(false);
        }
        popupWindow.dismiss();
    }

    public void showDropDown() {
        if (!isArrowHidden) {
            animateArrow(true);
        }
        popupWindow.showAsDropDown(this);
    }

    public void setTintColor(@ColorRes int resId) {
        if (arrowDrawable != null && !isArrowHidden) {
            DrawableCompat.setTint(arrowDrawable, ContextCompat.getColor(getContext(), resId));
        }
    }

    public void hideArrow() {
        isArrowHidden = true;
        setArrowDrawableOrHide(arrowDrawable);
    }

    public void showArrow() {
        isArrowHidden = false;
        setArrowDrawableOrHide(arrowDrawable);
    }

    public boolean isArrowHidden() {
        return isArrowHidden;
    }

    public void setDropDownListPaddingBottom(int paddingBottom) {
        dropDownListPaddingBottom = paddingBottom;
    }

    public int getDropDownListPaddingBottom() {
        return dropDownListPaddingBottom;
    }

    public void setSpinnerTextFormatter(SpinnerTextFormatter spinnerTextFormatter) {
        this.spinnerTextFormatter = spinnerTextFormatter;
    }
}
