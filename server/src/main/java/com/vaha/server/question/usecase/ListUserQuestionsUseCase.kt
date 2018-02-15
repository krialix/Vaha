package com.vaha.server.question.usecase

import com.google.api.server.spi.response.CollectionResponse
import com.google.appengine.api.datastore.Cursor
import com.google.appengine.api.datastore.QueryResultIterator
import com.googlecode.objectify.Key
import com.vaha.server.base.UseCase
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.client.SessionClient
import com.vaha.server.question.entity.Question
import com.vaha.server.question.entity.Question.QuestionStatus
import com.vaha.server.user.entity.Account

class ListUserQuestionsUseCase(
    private val cursor: String?,
    private val status: QuestionStatus,
    private val userId: String
) : UseCase<CollectionResponse<SessionClient>> {

  override fun run(): CollectionResponse<SessionClient> {
    val qi = getQueryResultIterator()

    return qi
        .asSequence()
        .map { it.copy(owner = true, requestSent = false) }
        .map { SessionClient.from(it) }
        .toList()
        .let {
          CollectionResponse.builder<SessionClient>()
              .setItems(it)
              .setNextPageToken(qi.cursor.toWebSafeString())
              .build()
        }
  }

  private fun getQueryResultIterator(): QueryResultIterator<Question> {
    var query = ofy()
        .load()
        .type(Question::class.java)
        .ancestor(Key.create<Account>(userId))
        .filter(Question.FIELD_STATUS, status)
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