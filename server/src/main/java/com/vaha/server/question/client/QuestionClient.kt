package com.vaha.server.question.client

import com.vaha.server.category.client.CategoryClient
import com.vaha.server.question.entity.Question
import org.joda.time.DateTime

data class QuestionClient(
    val id: String,
    val user: QuestionOwnerClient,
    val category: CategoryClient,
    val content: String,
    val answerer: AnswererClient?,
    val createdAt: DateTime?,
    val isRequestSent: Boolean,
    val isRequestEnabled: Boolean,
    val pendingUserRequests: List<PendingUserRequestClient>?
) {
    data class QuestionOwnerClient(val id: String, val username: String, val isOwner: Boolean)

    data class PendingUserRequestClient(val id: String, val username: String, val rating: String)

    data class AnswererClient(val id: String, val username: String)

    companion object {
        fun from(
            question: Question,
            isOwner: Boolean = false,
            requestSent: Boolean = false,
            requestEnabled: Boolean = true
        ): QuestionClient {
            return QuestionClient(
                id = question.key.toWebSafeString(),
                user = QuestionOwnerClient(
                    id = question.ownerWebsafeId,
                    username = question.username,
                    isOwner = isOwner
                ),
                category = CategoryClient.from(question.category.value),
                content = question.content,
                answerer = question.answerer?.let {
                    val answerer = it.get()
                    AnswererClient(id = answerer.websafeId, username = answerer.username)
                },
                createdAt = question.createdAt,
                isRequestSent = requestSent,
                isRequestEnabled = requestEnabled,
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