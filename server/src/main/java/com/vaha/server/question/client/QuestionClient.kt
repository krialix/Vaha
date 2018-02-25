package com.vaha.server.question.client

import com.vaha.server.question.entity.Question
import java.util.*

data class QuestionClient(
    val id: String?,
    val ownerId: String?,
    val owner: Boolean,
    val answererId: String?,
    val username: String?,
    val content: String?,
    val categoryId: String?,
    val categoryNameEn: String?,
    val categoryNameTr: String?,
    val status: String,
    val created: Date?,
    val requestSent: Boolean
) {

    constructor(question: Question) : this(
        question.websafeId,
        question.ownerWebsafeId,
        question.owner,
        question.answererWebsafeId,
        question.username,
        question.content,
        question.categoryWebsafeId,
        question.categoryNameEn,
        question.categoryNameTr,
        question.questionStatus.name,
        Date(question.createdAt.millis),
        question.requestSent
    )
}