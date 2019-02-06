package org.angmarch.views;

public interface DataProviderDelegate<T> {

    int getCount();

    T getItem(int position);

    T getItemInDataset(int position);

    int getAdjustedPosition(int position);
}
