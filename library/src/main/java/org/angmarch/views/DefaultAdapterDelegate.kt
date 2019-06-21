package org.angmarch.views

interface AdapterDelegate<T> {
    val count: Int

    fun getItem(position: Int, selectedIndex: Int): T
    fun getItemInDataset(position: Int): T
}

class DefaultAdapterDelegate<T>(private val items: List<T>) : AdapterDelegate<T> {

    override val count: Int = items.size - 1

    override fun getItem(position: Int, selectedIndex: Int): T {
        return if (position >= selectedIndex) {
            items[position + 1]
        } else {
            items[position]
        }
    }

    override fun getItemInDataset(position: Int): T = items[position]
}
