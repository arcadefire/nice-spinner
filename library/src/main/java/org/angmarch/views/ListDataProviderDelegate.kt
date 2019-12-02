package org.angmarch.views

internal class ListDataProviderDelegate<T>(private val list: List<T>) : DataProviderDelegate<T> {

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): T = list[position]
}