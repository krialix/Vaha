package com.vaha.android.data.entity.chat

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import com.vaha.android.util.NoArgs
import java.util.*

@NoArgs
data class Message(
    @get:Exclude val id: String? = null,
    val userId: String,
    val username: String,
    val message: String,
    val timestamp: Any = ServerValue.TIMESTAMP
) {
    @Exclude
    fun getTimestampDate(): Date = Date(timestamp as Long)
}