package com.vaha.android.components

import com.airbnb.epoxy.SimpleEpoxyModel
import com.vaha.android.R

class CategoryListHeaderModel : SimpleEpoxyModel(R.layout.view_category_list_header) {
    override fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int = 2
}
