package org.angmarch.views

import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

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
internal class NiceSpinnerAdapter<T>(
        private val adapterDelegate: AdapterDelegate<T>,
        private val viewDelegate: ViewDelegate<T>
) : BaseAdapter() {

    var selectedIndex: Int = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return viewDelegate.getView(position, getItem(position), convertView, parent)
    }

    fun getItemInDataset(position: Int): T = adapterDelegate.getItemInDataset(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItem(position: Int): T = adapterDelegate.getItem(position, selectedIndex)

    override fun getCount(): Int = adapterDelegate.count
}
