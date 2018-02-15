package com.vaha.server.question.client

import com.vaha.server.question.entity.Question
import java.util.*

data class SessionClient(
    val id: String?,
    val ownerId: String?,
    val answererId: String?,
    val answererUsername: String?,
    val username: String?,
    val content: String?,
    val categoryNameEn: String?,
    val categoryNameTr: String?,
    val created: Date?,
    val pendingAnswerers: Set<Question.PendingAnswerer>
) {

  companion object {
    fun from(question: Question): SessionClient {
      return SessionClient(
          id = question.websafeId,
          ownerId = question.ownerWebsafeId,
          username = question.username,
          answererId = question.answererWebsafeId,
          answererUsername = question.answererUsername,
          content = question.content,
          categoryNameEn = question.categoryNameEn,
          categoryNameTr = question.categoryNameTr,
          created = question.createdAt.toDate(),
          pendingAnswerers = question.pendingAnswerers
      )
    }
  }
}