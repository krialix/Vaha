package com.vaha.android.domain.models

import com.vaha.android.data.entity.Category

data class CategoryItem(private val client: Category) : ParamEpoxyItem<Category>() {

    override fun id(): String = client.id.toString()

    override fun item(): Category = client
}