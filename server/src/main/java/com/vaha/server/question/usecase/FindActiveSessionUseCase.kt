package com.vaha.server.question.usecase

import com.googlecode.objectify.Key
import com.vaha.server.base.UseCase
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.client.QuestionClient
import com.vaha.server.user.entity.Account

class FindActiveSessionUseCase(private val userId: String): UseCase<QuestionClient?> {

  override fun run(): QuestionClient? {
    val userKey = Key.create<Account>(userId)

    val activeQuestionKeys = ofy().load().key(userKey).now().activeQuestionKeys

    if (activeQuestionKeys.isEmpty()) return null

    val questionKey = activeQuestionKeys.find { it.getParent<Account>().equivalent(userKey) }

    return questionKey?.let {
      var question = ofy().load().key(it).now()
      question = question.copy(owner = question.parent.equivalent(userKey))
      QuestionClient(question)
    }
  }
}