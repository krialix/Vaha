package com.vaha.server.question.usecase

import com.google.api.server.spi.response.CollectionResponse
import com.google.appengine.api.datastore.Cursor
import com.google.appengine.api.datastore.QueryResultIterator
import com.googlecode.objectify.Key
import com.vaha.server.base.UseCase
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.client.QuestionClient
import com.vaha.server.question.entity.Question
import com.vaha.server.question.entity.UserSessionRel
import com.vaha.server.user.entity.Account

class ListAvailableQuestionsUseCase(
    private val cursor: String?,
    private val userId: String,
    private val questionSort: ListAvailableQuestionsUseCase.QuestionSort
) : UseCase<CollectionResponse<QuestionClient>> {

    override fun run(): CollectionResponse<QuestionClient> {
        val requesterKey = Key.create<Account>(userId)

        val questions = when (questionSort) {
            ListAvailableQuestionsUseCase.QuestionSort.AVAILABLE -> getAvailableQuestions()
            ListAvailableQuestionsUseCase.QuestionSort.PENDING -> getQuestionsBySessionStatus(
                requesterKey,
                UserSessionRel.SessionStatus.PENDING
            )
            ListAvailableQuestionsUseCase.QuestionSort.IN_PROGRESS -> getQuestionsBySessionStatus(
                requesterKey,
                UserSessionRel.SessionStatus.IN_PROGRESS
            )
            ListAvailableQuestionsUseCase.QuestionSort.COMPLETED -> getQuestionsBySessionStatus(
                requesterKey,
                UserSessionRel.SessionStatus.COMPLETED
            )
        }

        return questions
            .asSequence()
            .map {
                it.owner = it.isOwner(requesterKey)
                return@map QuestionClient(it)
            }
            .toList()
            .let {
                CollectionResponse.builder<QuestionClient>()
                    .setItems(it)
                    .build()
            }
    }

    private fun getAvailableQuestions(): List<Question> {
        var query = ofy()
            .load()
            .type(Question::class.java)
            .filter(Question.FIELD_STATUS, Question.QuestionStatus.AVAILABLE)
            .order("-${Question.FIELD_CREATED_AT}")

        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor))
        }

        query = query.limit(DEFAULT_LIST_LIMIT)
        query.iterator().cursor

        return query.limit(DEFAULT_LIST_LIMIT).list()
    }

    private fun getQuestionsBySessionStatus(
        userKey: Key<Account>,
        sessionStatus: UserSessionRel.SessionStatus
    ): List<Question> {
        var query = ofy()
            .load()
            .type(UserSessionRel::class.java)
            .filter("userKey", userKey)
            .filter("sessionStatus", sessionStatus)

        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor))
        }
        return query.limit(DEFAULT_LIST_LIMIT)
            .keys()
            .iterable()
            .map { Key.create<Question>(it.name) }
            .let { ofy().load().keys(it) }
            .map { it.value }
    }

    private fun getQueryResultIterator(): QueryResultIterator<Question> {
        var query = ofy()
            .load()
            .type(Question::class.java)
            .filter(Question.FIELD_STATUS, Question.QuestionStatus.AVAILABLE)
            .order("-${Question.FIELD_CREATED_AT}")

        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor))
        }
        query = query.limit(DEFAULT_LIST_LIMIT)
        return query.iterator()
    }

    companion object {
        private const val DEFAULT_LIST_LIMIT = 100
    }

    enum class QuestionSort {
        AVAILABLE,
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }
}