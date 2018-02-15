package com.vaha.android.data.entity

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import com.vaha.android.data.entity.chat.Message
import java.util.*

data class SessionMessage(
    private val userId: String,
    private val userAvatar: String,
    private val username: String,
    private val messageId: String,
    private val messageText: String,
    private val messageCreatedAt: Date
) : IMessage {

    constructor(message: Message) : this(
        message.userId,
        "",
        message.username,
        message.id!!,
        message.message,
        message.getTimestampDate()
    )

    override fun getId(): String = messageId

    override fun getCreatedAt(): Date = messageCreatedAt

    override fun getUser(): IUser = SessionUser(userId, userAvatar, username)

    override fun getText(): String = messageText

    class SessionUser(
        private val userId: String,
        private val userAvatar: String,
        private val username: String
    ) : IUser {
        override fun getAvatar(): String = userAvatar

        override fun getName(): String = username

        override fun getId(): String = userId
    }
}