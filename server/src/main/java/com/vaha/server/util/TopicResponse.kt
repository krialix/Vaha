package com.vaha.server.util

data class TopicResponse(
    val displayName: String,
    val displayNameTr: String,
    val topicName: String,
    val subscribed: Boolean
)