package com.vaha.server.question.usecase

import com.googlecode.objectify.Key
import com.vaha.server.base.UseCase
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.client.QuestionClient
import com.vaha.server.user.entity.Account

class ListSessionsUseCase(private val userId: String) : UseCase<Collection<QuestionClient>> {

    override fun run(): Collection<QuestionClient> {
        val requesterKey = Key.create<Account>(userId)
        val user = ofy().load().key(requesterKey).now()

        return ofy()
            .load()
            .keys(user.questionKeys)
            .values
            .map { it.copy(owner = it.parent.equivalent(requesterKey)) }
            .map { QuestionClient(it) }
    }
}