package com.vaha.android.data.entity

import com.squareup.moshi.Json

data class Category(

    @Json(name = "id")
    val id: Long,

    @Json(name = "image")
    val image: String,

    @Json(name = "displayName")
    val displayName: String
)