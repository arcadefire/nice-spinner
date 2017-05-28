package org.angmarch.views;

import android.content.Context;
import android.widget.ListAdapter;

public class NiceSpinnerAdapterWrapper extends NiceSpinnerBaseAdapter {

    private final ListAdapter baseAdapter;

    NiceSpinnerAdapterWrapper(Context context, ListAdapter toWrap, int textColor, int backgroundSelector) {
        super(context, textColor, backgroundSelector);
        baseAdapter = toWrap;
    }

    @Override public int getCount() {
        return baseAdapter.getCount() - 1;
    }

    @Override public Object getItem(int position) {
        return baseAdapter.getItem(position >= selectedIndex ? position + 1 : position);
    }

    @Override public Object getItemInDataset(int position) {
        return baseAdapter.getItem(position);
    }
}