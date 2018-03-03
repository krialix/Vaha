package com.vaha.server.category.client

import com.vaha.server.category.entity.Category

data class CategoryClient(
    val id: String,
    val displayName: String,
    val image: String
) {

    companion object {
        fun from(category: Category): CategoryClient = CategoryClient(
            category.key.toWebSafeString(),
            category.displayName,
            category.image
        )
    }
}