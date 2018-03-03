package com.vaha.server.question.client

import com.vaha.server.question.entity.Question
import org.joda.time.DateTime

data class QuestionClient(
    val id: String,
    val owner: QuestionOwnerClient,
    val categoryId: Long,
    val content: String,
    val answerer: AnswererClient?,
    val createdAt: DateTime?,
    val requestSent: Boolean,
    val pendingUserRequests: List<PendingUserRequestClient>?
) {
    data class QuestionOwnerClient(
        val id: String,
        val username: String,
        val isOwner: Boolean
    )

    data class PendingUserRequestClient(val id: String, val username: String, val rating: String)

    data class AnswererClient(val id: String, val username: String)

    companion object {
        fun from(
            question: Question,
            isOwner: Boolean = false,
            requestSent: Boolean = false
        ): QuestionClient {
            return QuestionClient(
                id = question.key.toWebSafeString(),
                owner = QuestionOwnerClient(
                    id = question.ownerWebsafeId,
                    username = question.username,
                    isOwner = isOwner
                ),
                categoryId = question.category.key.id,
                content = question.content,
                answerer = question.answerer?.let {
                    val answerer = it.get()
                    AnswererClient(id = answerer.websafeId, username = answerer.username)
                },
                createdAt = question.createdAt,
                requestSent = requestSent,
                pendingUserRequests = question.pendingUserRequests
                    .map {
                        PendingUserRequestClient(
                            id = it.userKey.toWebSafeString(),
                            username = it.username,
                            rating = "${it.rating}/5.0"
                        )
                    }
            )
        }
    }
}