package com.vaha.android.data.entity

import android.util.Base64
import com.squareup.moshi.Moshi

data class RegisterPayload(
    val username: String,
    val email: String,
    val password: String,
    val fcmToken: String
) {
    fun convertToBase64String(): String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<RegisterPayload>(RegisterPayload::class.java)
        val json = jsonAdapter.toJson(this)

        return Base64.encodeToString(json.toByteArray(), Base64.DEFAULT)
    }
}
