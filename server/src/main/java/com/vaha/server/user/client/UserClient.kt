package com.vaha.server.user.client

import com.vaha.server.user.entity.Account

data class UserClient(
    val id: String?,
    val username: String?,
    val rating: String,
    val availableQuestionCount: Int,
    val questionCount: Int?,
    val answerCount: Int?
) {

    companion object {
        fun from(account: Account) = UserClient(
            id = account.websafeId,
            username = account.username,
            rating = account.userRating.rating.toString(),
            availableQuestionCount = account.dailyQuestionCount,
            questionCount = account.questionCount,
            answerCount = account.answerCount
        )
    }
}