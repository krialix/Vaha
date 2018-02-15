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

    @Deprecated(message = "Use rating class") var rating: Float = 0.0F,
    @Deprecated(message = "Use rating class") var totalRater: Int = 0,

    var userRating: Account.Rating = Rating(),

    var totalDepositAmount: Money = Money.zero(CurrencyUnit.of("TRY")),
    var fcmToken: String,
    var createdAt: DateTime = DateTime.now(UTC),
    var activeQuestionKeys: @JvmSuppressWildcards List<Key<Question>> = emptyList(),
    var dailyQuestionCount: Int = 5,
    var questionKeys: @JvmSuppressWildcards List<Key<Question>> = emptyList()
) {

  val key: Key<Account>
    get() = Key.create(Account::class.java, id)

  val websafeId: String
    get() = key.toWebSafeString()

  fun resetToDefault(): Account {
    return copy(questionCount = 0,
        answerCount = 0,
        userRating = Account.Rating(),
        activeQuestionKeys = emptyList(),
        questionKeys = emptyList(),
        dailyQuestionCount = 5)
  }

  @OnLoad
  fun onLoad() {
    activeQuestionKeys = if (activeQuestionKeys == null) emptyList() else activeQuestionKeys
    questionKeys = if (questionKeys == null) emptyList() else questionKeys
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
        return Rating(oldRaterCount, newRating)
      }
    }
  }
}
