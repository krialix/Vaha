package com.vaha.server.question.usecase

import com.google.api.server.spi.response.CollectionResponse
import com.google.appengine.api.datastore.Cursor
import com.google.appengine.api.datastore.QueryResultIterator
import com.googlecode.objectify.Key
import com.vaha.server.base.UseCase
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.client.QuestionClient
import com.vaha.server.question.entity.Question
import com.vaha.server.user.entity.Account

class ListAvailableQuestionsUseCase(
    private val cursor: String?,
    private val userId: String
) : UseCase<CollectionResponse<QuestionClient>> {

  override fun run(): CollectionResponse<QuestionClient> {
    val requesterKey = Key.create<Account>(userId)

    val qi = getQueryResultIterator()

    return qi
        .asSequence()
        .map {
          it.copy(owner = it.isOwner(requesterKey), requestSent = it.isRequestSent(requesterKey))
        }
        .map { QuestionClient(it) }
        .toList()
        .let {
          CollectionResponse.builder<QuestionClient>()
              .setItems(it)
              .setNextPageToken(qi.cursor.toWebSafeString())
              .build()
        }
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
}