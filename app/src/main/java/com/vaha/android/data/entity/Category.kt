package com.vaha.android.data.entity

import com.squareup.moshi.Json

data class Category(

    @Json(name = "image")
    val image: String,

    @Json(name = "displayNameTr")
    val displayNameTr: String,

    @Json(name = "displayNameEn")
    val displayNameEn: String,

    @Json(name = "id")
    val id: String
)