package com.vaha.server.category.entity

import com.googlecode.objectify.Key
import com.googlecode.objectify.annotation.Cache
import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.vaha.server.common.annotations.NoArgs
import com.vaha.server.ofy.OfyService.factory

@Entity
@Cache
@NoArgs
data class Category(
    @Id var id: Long = allocateId(),
    var displayName: String,
    var displayNameTr: String,
    var image: String,
    var questionCount: Int = 0
) {

    val key: Key<Category> = Key.create(Category::class.java, id)

    val websafeId: String = key.toWebSafeString()

    val topicName: String =
        displayName
            .replace(" ", "_")
            .replace("&", "and")
            .toLowerCase()
            .trim()

    fun resetToDefault(): Category {
        return copy(questionCount = 0)
    }

    companion object {
        fun allocateId(): Long = factory().allocateId(Category::class.java).id
    }
}