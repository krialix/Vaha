package com.vaha.android.domain.models

import com.vaha.android.data.entity.Category

data class CategoryItem(private val client: Category) : ParamEpoxyItem<Category>() {

    override fun id(): String = client.id

    override fun item(): Category = client
}