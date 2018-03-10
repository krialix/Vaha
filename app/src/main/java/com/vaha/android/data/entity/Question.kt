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

    @Json(name = "isRequestSent")
    val isRequestEnabled: Boolean,

    @Json(name = "content")
    val content: String,

    @Json(name = "requests")
    val requests: List<Request>?
) {

    data class Answerer(

        @Json(name = "id")
        val userId: String,

        @Json(name = "displayName")
        val displayName: String
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

        @Json(name = "id")
        val id: Long,

        @Json(name = "nameTr")
        val displayName: String,

        @Json(name = "image")
        val image: String

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