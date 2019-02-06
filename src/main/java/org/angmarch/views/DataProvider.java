package org.angmarch.views;

public interface DataProvider<T> {

    int getCount();

    T getItem(int position);

    T getItemInDataset(int position);

    int getAdjustedPosition(int position);
}
