package com.vaha.android.data.api

import com.vaha.server.vahaApi.VahaApi
import com.vaha.server.vahaApi.model.CollectionResponseQuestionClient
import com.vaha.server.vahaApi.model.QuestionClient
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionService @Inject constructor(private val vahaApi: VahaApi) {

    fun insertQuestion(content: String, categoryId: String): Single<QuestionClient> =
        Single.fromCallable { vahaApi.questions().insert(content, categoryId).execute() }

    fun listAvailableQuestions(cursor: String?): Single<CollectionResponseQuestionClient> =
        Single.fromCallable { vahaApi.questions().list().setCursor(cursor).execute() }
}