package com.vaha.server.user

import com.google.api.server.spi.ServiceException
import com.google.api.server.spi.auth.common.User
import com.google.api.server.spi.config.ApiMethod
import com.google.api.server.spi.config.ApiMethod.HttpMethod.*
import com.google.api.server.spi.config.Named
import com.google.api.server.spi.response.CollectionResponse
import com.google.appengine.api.urlfetch.URLFetchServiceFactory
import com.googlecode.objectify.Key
import com.vaha.server.base.BaseEndpoint
import com.vaha.server.category.entity.Category
import com.vaha.server.endpointsvalidator.EndpointsValidator
import com.vaha.server.endpointsvalidator.validator.AuthValidator
import com.vaha.server.endpointsvalidator.validator.BadRequestValidator
import com.vaha.server.notification.NotificationService
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.client.QuestionClient
import com.vaha.server.question.entity.Question
import com.vaha.server.user.client.UserClient
import com.vaha.server.user.entity.Account
import com.vaha.server.user.usecase.GetUserUseCase
import com.vaha.server.user.usecase.InsertUserUseCase
import com.vaha.server.user.usecase.UpdateFcmTokenUseCase
import com.vaha.server.util.TopicResponse
import javax.annotation.Nullable

internal class MeEndpoint : BaseEndpoint() {

    private val notificationService by lazy(LazyThreadSafetyMode.NONE) {
        NotificationService(URLFetchServiceFactory.getURLFetchService())
    }

    @ApiMethod(name = "me.register", path = "me", httpMethod = POST)
    @Throws(ServiceException::class)
    fun register(@Named("payload") base64Payload: String?): UserClient {

        EndpointsValidator().on(
            BadRequestValidator.create(
                base64Payload,
                "payload cannot be null."
            )
        )

        return InsertUserUseCase(base64Payload!!, notificationService).run()
    }

    @ApiMethod(name = "me.get", path = "me", httpMethod = GET)
    @Throws(ServiceException::class)
    fun get(user: User?): UserClient {

        EndpointsValidator().on(AuthValidator.create(user))

        return GetUserUseCase(user?.id).run()
    }

    @ApiMethod(name = "me.updateFcmToken", path = "me/updateFcmToken", httpMethod = POST)
    @Throws(ServiceException::class)
    fun updateFcmToken(@Named("fcmToken") fcmToken: String?, user: User?) {

        EndpointsValidator()
            .on(BadRequestValidator.create(fcmToken, "fcmToken is empty"))
            .on(AuthValidator.create(user))

        UpdateFcmTokenUseCase(fcmToken!!, user?.id!!).run()
    }

    @ApiMethod(name = "me.listFcmTopics", path = "me/listFcmTopics", httpMethod = GET)
    @Throws(ServiceException::class)
    fun listFcmTopics(user: User?): Collection<TopicResponse> {

        EndpointsValidator().on(AuthValidator.create(user))

        val account = ofy().load().key(Key.create<Account>(user?.id)).now()

        val listSubscribedTopics = notificationService.listSubscribedTopics(account.fcmToken)
            .let { it?.rel?.topics }

        return ofy()
            .load()
            .type(Category::class.java)
            .iterable()
            .map {
                TopicResponse(
                    displayName = it.displayName,
                    topicName = it.topicName,
                    subscribed = listSubscribedTopics?.contains(it.topicName) ?: false
                )
            }
    }

    @ApiMethod(name = "me.subscribeTopic", path = "me/subscribeTopic", httpMethod = POST)
    @Throws(ServiceException::class)
    fun subscribeTopic(@Named("topicName") topicName: String, user: User?) {

        EndpointsValidator().on(AuthValidator.create(user))

        val account = ofy().load().key(Key.create<Account>(user?.id)).now()

        notificationService.subscribeTopic(topicName, account.fcmToken)
    }

    @ApiMethod(name = "me.unsubscribeTopic", path = "me/unsubscribeTopic", httpMethod = DELETE)
    @Throws(ServiceException::class)
    fun unsubscribeTopic(@Named("topicName") topicName: String, user: User?) {

        EndpointsValidator().on(AuthValidator.create(user))

        val account = ofy().load().key(Key.create<Account>(user?.id)).now()

        notificationService.unsubscribeTopic(topicName, account.fcmToken)
    }

    @ApiMethod(name = "me.listQuestions", path = "me/listQuestions", httpMethod = GET)
    @Throws(ServiceException::class)
    fun listQuestions(
        @Nullable @Named("cursor") cursor: String?,
        @Named("status") status: Question.Status,
        user: User?
    ): CollectionResponse<QuestionClient>? {

        EndpointsValidator().on(AuthValidator.create(user))

        //return ListUserQuestionsUseCase(cursor, status, user!!.id).run()

        return null
    }
}