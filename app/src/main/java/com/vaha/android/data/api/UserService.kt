package com.vaha.android.data.api

import com.vaha.server.vahaApi.VahaApi
import com.vaha.server.vahaApi.model.TopicResponseCollection
import com.vaha.server.vahaApi.model.UserClient
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService @Inject constructor(private val vahaApi: VahaApi) {

    fun getMe(): Single<UserClient> = Single.fromCallable { vahaApi.me().get().execute() }

    fun registerUser(payload: String): Single<UserClient> =
        Single.fromCallable { vahaApi.me().register(payload).execute() }

    fun updateFcmToken(fcmToken: String): Completable =
        Completable.fromAction { vahaApi.me().updateFcmToken(fcmToken).execute() }

    fun listFcmTopics(): Single<TopicResponseCollection> =
        Single.fromCallable { vahaApi.me().listFcmTopics().execute() }
}
