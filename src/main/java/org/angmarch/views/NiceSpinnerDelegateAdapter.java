package org.angmarch.views;

import android.content.Context;

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
public class NiceSpinnerDelegateAdapter<T> extends NiceSpinnerBaseAdapter {

    private final DataProviderDelegate<T> delegate;

    NiceSpinnerDelegateAdapter(
            Context context,
            int textColor,
            int backgroundSelector,
            SpinnerTextFormatter spinnerTextFormatter,
            DataProviderDelegate<T> delegate
    ) {
        super(context, textColor, backgroundSelector, spinnerTextFormatter);
        this.delegate = delegate;
    }

    @Override
    public int getCount() {
        return delegate.getCount();
    }

    @Override
    public T getItem(int position) {
        return delegate.getItem(position);
    }

    @Override
    public T getItemFromList(int position) {
        return delegate.getItemInDataset(position);
    }

    @Override
    public int getAdjustedPosition(int position) {
        return delegate.getAdjustedPosition(position);
    }
}