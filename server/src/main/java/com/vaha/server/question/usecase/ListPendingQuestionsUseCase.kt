package com.vaha.server.question.usecase

import com.google.api.server.spi.response.CollectionResponse
import com.googlecode.objectify.Key
import com.vaha.server.base.UseCase
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.client.QuestionClient
import com.vaha.server.user.entity.Account

class ListPendingQuestionsUseCase(
    private val cursor: String?,
    private val userId: String
) : UseCase<CollectionResponse<QuestionClient>> {

    override fun run(): CollectionResponse<QuestionClient> {
        val requesterKey = Key.create<Account>(userId)

        val account = ofy().load().key(requesterKey).now()

        val questions = ofy().load().keys(account.pendingQuestionKeys).values

        return questions
            .asSequence()
            .map { QuestionClient.from(question = it, requestSent = true) }
            .toList()
            .let {
                CollectionResponse.builder<QuestionClient>()
                    .setItems(it)
                    .setNextPageToken(null)
                    .build()
            }
    }
}