package org.angmarch.views

import android.content.Context

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
class NiceSpinnerAdapter<T> internal constructor(
        context: Context?,
        private val items: List<T>,
        textColor: Int,
        backgroundSelector: Int,
        spinnerTextFormatter: SpinnerTextFormatter<*>,
        horizontalAlignment: PopUpTextAlignment
) : NiceSpinnerBaseAdapter<Any?>(context, textColor, backgroundSelector, spinnerTextFormatter, horizontalAlignment) {
    override fun getCount(): Int {
        return items.size - 1
    }

    override fun getItem(position: Int): Any {
//        return items.get(position);
        return if (position >= selectedIndex) {
            items[position + 1] as Any
        } else {
            items[position] as Any
        }
    }

    override fun getItemInDataset(position: Int): Any {
        return items[position]  as Any
    }
}