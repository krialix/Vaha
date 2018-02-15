package com.vaha.android.data.entity

import com.squareup.moshi.Json

data class Question(

    @Json(name = "createdAt")
    val createdAt: String,

    @Json(name = "answerer")
    val answerer: Answerer?,

    @Json(name = "id")
    val id: String,

    @Json(name = "category")
    val category: Category,

    @Json(name = "user")
    val user: User,

    @Json(name = "isRequestSent")
    val isRequestSent: Boolean,

    @Json(name = "content")
    val content: String,

    @Json(name = "requests")
    val requests: List<Request>?
) {

    data class Answerer(

        @Json(name = "displayName")
        val displayName: String,

        @Json(name = "userId")
        val userId: String
    )

    data class User(

        @Json(name = "isOwner")
        val isOwner: Boolean,

        @Json(name = "displayName")
        val displayName: String,

        @Json(name = "userId")
        val userId: String
    )

    data class Category(

        @Json(name = "nameTr")
        val nameTr: String,

        @Json(name = "nameEn")
        val nameEn: String,

        @Json(name = "categoryId")
        val categoryId: String
    )

    data class Request(

        @Json(name = "displayName")
        val displayName: String,

        @Json(name = "userId")
        val userId: String,

        @Json(name = "rating")
        val rating: String
    )
}