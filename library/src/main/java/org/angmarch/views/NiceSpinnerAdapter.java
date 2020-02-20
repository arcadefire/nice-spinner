package org.angmarch.views;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

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
public class NiceSpinnerAdapter<T> extends BaseAdapter {

    private final SpinnerTextFormatter spinnerTextFormatter;
    private final PopUpTextAlignment horizontalAlignment;
    private final DataProviderDelegate<T> delegate;
    private final boolean showSelectedItemInDropDownList;

    private int textColor;
    private int backgroundSelector;
    private int defaultTextHeight;

    int selectedIndex = 0;

    NiceSpinnerAdapter(
            Context context,
            int textColor,
            int backgroundSelector,
            DataProviderDelegate<T> delegate,
            SpinnerTextFormatter spinnerTextFormatter,
            PopUpTextAlignment horizontalAlignment,
            boolean showSelectedItemInDropDownList
    ) {
        this.spinnerTextFormatter = spinnerTextFormatter;
        this.backgroundSelector = backgroundSelector;
        this.textColor = textColor;
        this.delegate = delegate;
        this.horizontalAlignment = horizontalAlignment;
        this.showSelectedItemInDropDownList = showSelectedItemInDropDownList;
    }

    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        TextView textView;

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.spinner_list_item, null);
            textView = convertView.findViewById(R.id.text_view_spinner);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textView.setBackground(ContextCompat.getDrawable(context, backgroundSelector));
            }
            convertView.setTag(new ViewHolder(textView));
        } else {
            textView = ((ViewHolder) convertView.getTag()).textView;
        }

        textView.setTextColor(textColor);
        textView.setText(spinnerTextFormatter.formatEntryText(getItem(position)));

        setTextHorizontalAlignment(textView);

        return convertView;
    }

    private void setTextHorizontalAlignment(TextView textView) {
        switch (horizontalAlignment) {
            case START:
                textView.setGravity(Gravity.START);
                break;
            case END:
                textView.setGravity(Gravity.END);
                break;
            case CENTER:
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                break;
        }
    }

    int getSelectedIndex() {
        return selectedIndex;
    }

    void setSelectedIndex(int index) {
        selectedIndex = index;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        if (showSelectedItemInDropDownList) {
            return delegate.getCount();
        } else {
            return Math.max(delegate.getCount() - 1, 0);
        }
    }

    @Override
    public T getItem(int position) {
        return delegate.getItem(getAdjustedPosition(position));
    }

    T getItemFromDataset(int position) {
        return delegate.getItem(position);
    }

    // The selected item is not displayed within the list, so when the selected position is equal to
    // the one of the currently selected item it gets shifted to the next item.
    int getAdjustedPosition(int position) {
        if (position >= selectedIndex && !showSelectedItemInDropDownList) {
            return position + 1;
        } else {
            return position;
        }
    }

    static class ViewHolder {
        TextView textView;

        ViewHolder(TextView textView) {
            this.textView = textView;
        }
    }
}
