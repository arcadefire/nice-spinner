package org.angmarch.views

interface DataProviderDelegate<T> {

    fun getCount(): Int

    fun getItem(position: Int): T
}