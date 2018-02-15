package com.vaha.android.data.entity.chat

import com.google.firebase.database.Exclude
import com.vaha.android.util.NoArgs
import java.util.*

@NoArgs
data class LastUserMessage(
    val senderId: String,
    val senderUsername: String,
    val questionId: String,
    val lastMessage: String,
    val timestamp: Any
) {
    @Exclude
    fun getTimestampDate(): Date = Date(timestamp as Long)

    companion object {
        val EMPTY_LAST_MESSAGE = LastUserMessage("", "", "", "", Unit)
    }
}