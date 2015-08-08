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

import java.util.ArrayList;

/**
 * @author angelo.marchesin
 */
@SuppressWarnings("unused")
public class NiceSpinner extends TextView {

    private static final int MAX_LEVEL = 10000;
    private static final int VIEW_LEFT = 0;
    private static final int VIEW_TOP = 1;
    private static final int DEFAULT_ELEVATION = 16;
    private static final String INSTANCE_STATE = "instance_state";
    private static final String SELECTED_INDEX = "selected_index";
    private static final String IS_POPUP_SHOWING = "is_popup_showing";

    private int mSelectedIndex;
    private Drawable mDrawable;
    private PopupWindow mPopup;
    private ListView mListView;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private int[] mViewBounds;
    private NiceSpinnerAdapter mAdapter;

    @SuppressWarnings("ConstantConditions")
    public NiceSpinner(Context context) {
        super(context);
        init(null);
    }

    public NiceSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public NiceSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(SELECTED_INDEX, mSelectedIndex);

        if (mPopup != null) {
            bundle.putBoolean(IS_POPUP_SHOWING, mPopup.isShowing());
            dismissDropDown();
        }

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable savedState) {
        if (savedState instanceof Bundle) {
            Bundle bundle = (Bundle) savedState;

            mSelectedIndex = bundle.getInt(SELECTED_INDEX);
            setText(mAdapter.getItemInDataset(mSelectedIndex).toString());
            mAdapter.notifyItemSelected(mSelectedIndex);

            if (bundle.getBoolean(IS_POPUP_SHOWING)) {
                if (mPopup != null) {
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

    private void init(AttributeSet attrs) {
        Resources resources = getResources();
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.NiceSpinner);
        int defaultPadding = resources.getDimensionPixelSize(R.dimen.one_and_a_half_grid_unit);

        setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        setPadding(resources.getDimensionPixelSize(R.dimen.three_grid_unit),
                defaultPadding, defaultPadding, defaultPadding);
        setClickable(true);
        setBackgroundResource(R.drawable.selector);

        mListView = new ListView(getContext());
        mListView.setDivider(null);
        mListView.setItemsCanFocus(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= mSelectedIndex && position < mAdapter.getCount()) {
                    position++;
                }

                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(parent, view, position, id);
                }

                mAdapter.notifyItemSelected(position);
                mSelectedIndex = position;
                setText(mAdapter.getItemInDataset(position).toString());
                dismissDropDown();
            }
        });

        mPopup = new PopupWindow(mListView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopup.setContentView(mListView);
        mPopup.setOutsideTouchable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPopup.setElevation(DEFAULT_ELEVATION);
            mPopup.setBackgroundDrawable(
                    ContextCompat.getDrawable(getContext(), R.drawable.spinner_drawable));
        } else {
            mPopup.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.drop_down_shadow));
        }

        mPopup.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float x = event.getRawX();
                float y = event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_OUTSIDE: {
                        // Compute bounds only once after the first event is fired
                        if (mViewBounds == null) {
                            mViewBounds = new int[2];
                            getLocationInWindow(mViewBounds);
                        }

                        if (isTouchInsideViewBounds(x, y, mViewBounds, NiceSpinner.this)
                                && mPopup.isShowing()) {
                            return true;
                        }

                        dismissDropDown();
                    }
                    break;
                }

                return false;
            }
        });

        Drawable basicDrawable = ContextCompat.getDrawable(getContext(), R.drawable.arrow);
        int resId = typedArray.getColor(R.styleable.NiceSpinner_arrowTint, -1);

        if (basicDrawable != null) {
            mDrawable = DrawableCompat.wrap(basicDrawable);

            if (resId != -1) {
                DrawableCompat.setTint(mDrawable, resId);
            }
        }
        setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawable, null);

        typedArray.recycle();
    }

    private static boolean isTouchInsideViewBounds(float x, float y, int[] viewBounds, View view) {
        return x < viewBounds[VIEW_LEFT] + view.getWidth() && x > viewBounds[VIEW_LEFT]
                && y < viewBounds[VIEW_TOP] + view.getHeight() && y > viewBounds[VIEW_TOP];
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void addOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public <T> void attachDataSource(@NonNull ArrayList<T> dataset) {
        mAdapter = new NiceSpinnerAdapter<>(getContext(), dataset);
        setAdapterInternal();
    }

    public void setAdapter(@NonNull ListAdapter adapter) {
        mAdapter = new NiceSpinnerAdapterWrapper(getContext(), adapter);
        setAdapterInternal();
    }

    private void setAdapterInternal() {
        mListView.setAdapter(mAdapter);
        setText(mAdapter.getItemInDataset(mSelectedIndex).toString());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mPopup.setWidth(View.MeasureSpec.getSize(widthMeasureSpec));
        mPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!mPopup.isShowing()) {
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
        ObjectAnimator animator = ObjectAnimator.ofInt(mDrawable, "level", start, end);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.start();
    }

    public void dismissDropDown() {
        animateArrow(false);
        mPopup.dismiss();
    }

    public void showDropDown() {
        animateArrow(true);
        mPopup.showAsDropDown(this);
    }

    public void setTintColor(@ColorRes int resId) {
        if (mDrawable != null) {
            DrawableCompat.setTint(mDrawable, getResources().getColor(resId));
        }
    }
}
