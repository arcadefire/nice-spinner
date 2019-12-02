package org.angmarch.views

import android.widget.ListAdapter

internal class AdapterDataProviderDelegate<T>(
        private val listAdapter: ListAdapter
) : DataProviderDelegate<T> {

    override fun getCount(): Int = listAdapter.count

    override fun getItem(position: Int): T = listAdapter.getItem(position) as T
}