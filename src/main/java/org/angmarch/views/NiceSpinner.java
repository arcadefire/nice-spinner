package org.angmarch.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

/**
 * @author angelo.marchesin
 */
@SuppressWarnings("unused")
public class NiceSpinner extends TextView {

    private static final int MAX_LEVEL = 10000;
    private static final int DEFAULT_ELEVATION = 16;
    private static final String INSTANCE_STATE = "instance_state";
    private static final String SELECTED_INDEX = "selected_index";
    private static final String IS_POPUP_SHOWING = "is_popup_showing";

    private int selectedIndex;
    private Drawable drawable;
    private PopupWindow popupWindow;
    private ListView listView;
    private NiceSpinnerBaseAdapter adapter;
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemSelectedListener onItemSelectedListener;
    private boolean isArrowHide;
    private int textColor;
    private int backgroundSelector;

    @SuppressWarnings("ConstantConditions")
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

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(SELECTED_INDEX, selectedIndex);
        if (popupWindow != null) {
            bundle.putBoolean(IS_POPUP_SHOWING, popupWindow.isShowing());
            dismissDropDown();
        }
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable savedState) {
        if (savedState instanceof Bundle) {
            Bundle bundle = (Bundle) savedState;
            selectedIndex = bundle.getInt(SELECTED_INDEX);

            if (adapter != null) {
                setText(adapter.getItemInDataset(selectedIndex).toString());
                adapter.notifyItemSelected(selectedIndex);
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
            savedState = bundle.getParcelable(INSTANCE_STATE);
        }
        super.onRestoreInstanceState(savedState);
    }

    /**
     * over write the setEnabled method,when you set true,you can see the arrow drawable and you can click this spinner;
     * when you set false, the arrow drawable will be hided and this spinner will do nothing if you click it.
     *
     * @param enabled
     */
    //the past version setEnabled is no use
    @Override
    public void setEnabled(boolean enabled) {
        boolean isEnable = isEnabled();
        if (isEnable != enabled) {
            //若设置无效,则不显示下拉小图标
            if (enabled) {
                isArrowHide = false;
                this.setCompoundArrowDrawable(drawable);
            } else {
                isArrowHide = true;
                this.setCompoundArrowDrawable(drawable);
            }
            super.setEnabled(enabled);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        Resources resources = getResources();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NiceSpinner);
        int defaultPadding = resources.getDimensionPixelSize(R.dimen.one_and_a_half_grid_unit);

        setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        setPadding(resources.getDimensionPixelSize(R.dimen.three_grid_unit), defaultPadding, defaultPadding,
                defaultPadding);
        setClickable(true);

        backgroundSelector = typedArray.getResourceId(R.styleable.NiceSpinner_backgroundSelector, R.drawable.selector);
        setBackgroundResource(backgroundSelector);
        textColor = typedArray.getColor(R.styleable.NiceSpinner_textTint, -1);
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

                adapter.notifyItemSelected(position);
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
                if (!isArrowHide) {
                    animateArrow(false);
                }
            }
        });

        isArrowHide = typedArray.getBoolean(R.styleable.NiceSpinner_hideArrow, false);
//        if (!isArrowHide) {
//            Drawable basicDrawable = ContextCompat.getDrawable(context, R.drawable.arrow);
//            int resId = typedArray.getColor(R.styleable.NiceSpinner_arrowTint, -1);
//            if (basicDrawable != null) {
//                drawable = DrawableCompat.wrap(basicDrawable);
//                if (resId != -1) {
//                    DrawableCompat.setTint(drawable, resId);
//                }
//            }
//            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
//        }

        //change the default value from -1 to integer max value
        //because -1 is the white color,event users never set the attr it will be changed too
        int drawableTint = typedArray.getColor(R.styleable.NiceSpinner_arrowTint, Integer.MAX_VALUE);
        //get the drawable res id, res id default value is 0
        //users can use their custom drawable to be the arrow drawable.
        int drawableId = typedArray.getResourceId(R.styleable.NiceSpinner_arrowDrawable, 0);
        //load and save the drawable
        drawable = this.initArrowDrawable(drawableId, drawableTint);
        //set the drawable to textView
        this.setCompoundArrowDrawable(drawable);

        typedArray.recycle();
    }

    //load and save the drawable
    private Drawable initArrowDrawable(int drawableId, int drawableTint) {
        Drawable basicDrawable = null;
        //if the drawable id is unset,load the system drawable
        if (drawableId != 0) {
            basicDrawable = this.getContext().getResources().getDrawable(drawableId);
        } else {
            basicDrawable = ContextCompat.getDrawable(this.getContext(), R.drawable.arrow);
        }
        if (basicDrawable != null) {
            basicDrawable = DrawableCompat.wrap(basicDrawable);
            //if the tint color is set,use the color
            if (drawableTint != Integer.MAX_VALUE && drawableTint != 0) {
                DrawableCompat.setTint(basicDrawable, drawableTint);
            }
        }

        return basicDrawable;
    }

    //set the arrow drawable to textView
    private boolean setCompoundArrowDrawable(Drawable drawable) {
        //if drawable is null, set nothing.
        if (!isArrowHide && drawable != null) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            return true;
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return false;
        }
    }

    /**
     * set arrow drawable resource id with tint color
     *
     * @param drawableId
     * @param tintColor
     */
    public void setArrowDrawable(@DrawableRes @ColorRes int drawableId, @ColorRes int tintColor) {
        Drawable arrowDrawable = this.initArrowDrawable(drawableId, tintColor);
        if (this.setCompoundArrowDrawable(arrowDrawable)) {
            drawable = arrowDrawable;
            this.invalidate();
        }
    }

    /**
     * set arrow drawable resource id
     *
     * @param drawableId
     */
    public void setArrowDrawable(@DrawableRes @ColorRes int drawableId) {
        Drawable arrowDrawable = this.initArrowDrawable(drawableId, 0);
        if (this.setCompoundArrowDrawable(arrowDrawable)) {
            drawable = arrowDrawable;
            this.invalidate();
        }
    }

    /**
     * set arrow drawable directly
     *
     * @param arrowDrawable
     */
    public void setArrowDrawable(Drawable arrowDrawable) {
        if (this.setCompoundArrowDrawable(arrowDrawable)) {
            drawable = arrowDrawable;
            this.invalidate();
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Set the default spinner item using its index
     *
     * @param position the item's position
     */
    public void setSelectedIndex(int position) {
        if (adapter != null) {
            if (position >= 0 && position <= adapter.getCount()) {
                adapter.notifyItemSelected(position);
                selectedIndex = position;
                setText(adapter.getItemInDataset(position).toString());
            } else {
                throw new IllegalArgumentException("Position must be lower than adapter count!");
            }
        }
    }

    public void addOnItemClickListener(@NonNull AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(@NonNull AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public <T> void attachDataSource(@NonNull List<T> dataset) {
        adapter = new NiceSpinnerAdapter<>(getContext(), dataset, textColor, backgroundSelector);
        setAdapterInternal(adapter);
    }

    public void setAdapter(@NonNull ListAdapter adapter) {
        this.adapter = new NiceSpinnerAdapterWrapper(getContext(), adapter, textColor, backgroundSelector);
        setAdapterInternal(this.adapter);
    }

    private void setAdapterInternal(@NonNull NiceSpinnerBaseAdapter adapter) {
        // If the adapter needs to be settled again, ensure to reset the selected index as well
        selectedIndex = 0;
        listView.setAdapter(adapter);
        setText(adapter.getItemInDataset(selectedIndex).toString());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        popupWindow.setWidth(View.MeasureSpec.getSize(widthMeasureSpec));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
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
        ObjectAnimator animator = ObjectAnimator.ofInt(drawable, "level", start, end);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.start();
    }

    public void dismissDropDown() {
        if (!isArrowHide) {
            animateArrow(false);
        }
        popupWindow.dismiss();
    }

    public void showDropDown() {
        if (!isArrowHide) {
            animateArrow(true);
        }
        popupWindow.showAsDropDown(this);
    }

    public void setTintColor(@ColorRes int resId) {
        if (drawable != null && !isArrowHide) {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), resId));
        }
    }
}
