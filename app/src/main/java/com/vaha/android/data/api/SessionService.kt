package com.vaha.android.data.api

import com.vaha.server.vahaApi.VahaApi
import com.vaha.server.vahaApi.model.CollectionResponseSessionClient
import com.vaha.server.vahaApi.model.QuestionClient
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionService @Inject constructor(private val vahaApi: VahaApi) {

    fun startSession(quesionId: String, answererId: String): Completable =
        Completable.fromAction { vahaApi.sessions().start(quesionId, answererId).execute() }

    fun endSession(quesionId: String, reasked: Boolean, rating: Int): Completable =
        Completable.fromAction { vahaApi.sessions().end(quesionId, rating, reasked).execute() }

    fun getSessions(status: String): Single<CollectionResponseSessionClient> =
        Single.fromCallable { vahaApi.me().listQuestions(status).execute() }

    fun sendAnswererReady(quesionId: String): Completable =
        Completable.fromAction { vahaApi.sessions().answererReady(quesionId).execute() }

    fun checkActiveSession(): Single<QuestionClient> =
        Single.fromCallable { vahaApi.sessions().activeSession().execute() ?: QuestionClient() }
}