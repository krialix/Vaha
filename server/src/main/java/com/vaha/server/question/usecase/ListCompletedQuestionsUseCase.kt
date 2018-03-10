package com.vaha.server.question.usecase

import com.google.api.server.spi.response.CollectionResponse
import com.google.appengine.api.datastore.Cursor
import com.googlecode.objectify.Key
import com.googlecode.objectify.Ref
import com.vaha.server.base.UseCase
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.client.QuestionClient
import com.vaha.server.question.entity.Question
import com.vaha.server.user.entity.Account

class ListCompletedQuestionsUseCase(
    private val cursor: String?,
    private val userId: String
) : UseCase<CollectionResponse<QuestionClient>> {

    override fun run(): CollectionResponse<QuestionClient> {
        val requesterKey = Key.create<Account>(userId)

        var query = ofy()
            .load()
            .type(Question::class.java)
            .filter(Question.FIELD_STATUS, Question.Status.COMPLETED)
            .filter(Question.FIELD_ANSWERER, Ref.create(requesterKey))
            .order("-${Question.FIELD_CREATED_AT}")

        cursor?.let { query = query.startAt(Cursor.fromWebSafeString(it)) }

        query = query.limit(DEFAULT_LIST_LIMIT)

        val resultIterator = query.iterator()

        val cursor = resultIterator.cursor.toWebSafeString()

        return resultIterator
            .asSequence()
            .map {
                QuestionClient.from(
                    question = it,
                    isOwner = it.isOwner(requesterKey),
                    requestSent = it.pendingUserRequests.any { it.userKey.equivalent(requesterKey) },
                    requestEnabled = false
                )
            }
            .toList()
            .let {
                CollectionResponse.builder<QuestionClient>()
                    .setItems(it)
                    .setNextPageToken(cursor)
                    .build()
            }
    }

    companion object {
        private const val DEFAULT_LIST_LIMIT = 100
    }
}