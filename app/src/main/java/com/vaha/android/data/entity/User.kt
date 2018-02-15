package com.vaha.android.data.entity

import com.squareup.moshi.Json

data class User(

    @Json(name = "questionCount")
    val questionCount: Int,

    @Json(name = "answerCount")
    val answerCount: Int,

    @Json(name = "rating")
    val rating: String,

    @Json(name = "availableQuestionCount")
    val availableQuestionCount: Int,

    @Json(name = "id")
    val id: String,

    @Json(name = "username")
    val username: String
)