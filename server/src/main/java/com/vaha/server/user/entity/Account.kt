package com.vaha.server.user.entity

import com.googlecode.objectify.Key
import com.googlecode.objectify.annotation.*
import com.vaha.server.common.annotations.NoArgs
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.entity.Question
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.joda.time.DateTime
import org.joda.time.DateTimeZone.UTC

@Entity
@Cache
@NoArgs
data class Account(
    @Id var id: Long = allocateId(),
    @Index var username: String,
    @Index var email: String,
    var questionCount: Int = 0,
    var answerCount: Int = 0,

    var userRating: Account.Rating = Rating(),

    var totalDepositAmount: Money = Money.zero(CurrencyUnit.of("TRY")),
    var fcmToken: String,
    var createdAt: DateTime = DateTime.now(UTC),
    var dailyQuestionCount: Int = 5,
    var inProgressQuestionKeys: @JvmSuppressWildcards MutableSet<Key<Question>> = mutableSetOf(),
    var pendingQuestionKeys: @JvmSuppressWildcards MutableSet<Key<Question>> = mutableSetOf()
) {

    val key: Key<Account>
        get() = Key.create(Account::class.java, id)

    val websafeId: String
        get() = key.toWebSafeString()

    val isDailyLimitZero: Boolean
        get() {
            return dailyQuestionCount == 0
        }

    fun resetToDefault(): Account {
        return copy(
            questionCount = 0,
            answerCount = 0,
            userRating = Account.Rating(),
            inProgressQuestionKeys = mutableSetOf(),
            pendingQuestionKeys = mutableSetOf(),
            dailyQuestionCount = 5
        )
    }

    @OnLoad
    fun onLoad() {
        inProgressQuestionKeys =
                if (inProgressQuestionKeys == null) mutableSetOf() else inProgressQuestionKeys
        pendingQuestionKeys =
                if (pendingQuestionKeys == null) mutableSetOf() else pendingQuestionKeys
    }

    companion object {
        const val FIELD_EMAIL = "email"
        const val FIELD_USERNAME = "username"

        private fun allocateId() = ofy().factory().allocateId(Account::class.java).id
    }

    data class Rating(val raterCount: Int = 0, val rating: Float = 0.0f) {
        companion object {
            fun calculateNewRating(oldRating: Rating, rating: Float): Rating {
                val oldRaterCount = oldRating.raterCount
                val newRaterCount = oldRaterCount.inc()
                val newRating = ((oldRating.rating * oldRaterCount) + rating) / newRaterCount
                return Rating(newRaterCount, newRating)
            }
        }
    }
}
