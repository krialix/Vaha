package com.vaha.server.api

import com.google.api.client.http.HttpMethods.POST
import com.google.api.server.spi.ServiceException
import com.google.api.server.spi.auth.common.User
import com.google.api.server.spi.config.ApiMethod
import com.google.api.server.spi.config.ApiMethod.HttpMethod.GET
import com.google.appengine.api.urlfetch.URLFetchServiceFactory
import com.googlecode.objectify.Key
import com.vaha.server.endpointsvalidator.EndpointsValidator
import com.vaha.server.endpointsvalidator.validator.AuthValidator
import com.vaha.server.notification.NotificationService
import com.vaha.server.notification.NotificationTypes
import com.vaha.server.notification.PushMessage
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.client.QuestionClient
import com.vaha.server.question.entity.Question
import com.vaha.server.question.usecase.EndSessionUseCase
import com.vaha.server.question.usecase.FindActiveSessionUseCase
import com.vaha.server.question.usecase.ListSessionsUseCase
import com.vaha.server.question.usecase.StartSessionUseCase
import com.vaha.server.user.entity.Account
import javax.inject.Named

internal class SessionEndpoint : BaseEndpoint() {

  private val notificationService: NotificationService by lazy(LazyThreadSafetyMode.NONE) {
    NotificationService(URLFetchServiceFactory.getURLFetchService())
  }

  @ApiMethod(name = "sessions.start", path = "sessions/start", httpMethod = POST)
  @Throws(ServiceException::class)
  fun start(
      @Named("questionId") questionId: String,
      @Named("answererId") answererId: String,
      user: User?) {

    EndpointsValidator().on(AuthValidator.create(user))

    StartSessionUseCase(questionId, user?.id!!, answererId, notificationService).run()
  }

  @ApiMethod(name = "sessions.end", path = "sessions/end", httpMethod = POST)
  @Throws(ServiceException::class)
  fun end(
      @Named("questionId") questionId: String,
      @Named("rating") rating: Int?,
      @Named("reasked") reasked: Boolean,
      user: User?) {

    EndpointsValidator().on(AuthValidator.create(user))

    EndSessionUseCase(questionId, rating!!.toFloat(), reasked, notificationService).run()
  }

  @ApiMethod(name = "sessions.list", path = "sessions", httpMethod = GET)
  @Throws(ServiceException::class)
  fun list(user: User?): Collection<QuestionClient> {

    EndpointsValidator().on(AuthValidator.create(user))

    return ListSessionsUseCase(user?.id!!).run()
  }

  @ApiMethod(name = "sessions.activeSession", path = "sessions/activeSession", httpMethod = GET)
  @Throws(ServiceException::class)
  fun findActiveSession(user: User?): QuestionClient? {

    EndpointsValidator().on(AuthValidator.create(user))

    return FindActiveSessionUseCase(user?.id!!).run()
  }

  @ApiMethod(name = "sessions.answererReady", path = "sessions/answererReady", httpMethod = POST)
  @Throws(ServiceException::class)
  fun answererReady(@Named("questionId") questionId: String, user: User?) {

    EndpointsValidator().on(AuthValidator.create(user))

    val questionOwnerKey = Key.create<Question>(questionId).getParent<Account>()
    val answererKey = Key.create<Account>(user!!.id)
    val questionKey = Key.create<Question>(questionId)

    val fetched = ofy().load().keys(questionOwnerKey, questionKey, answererKey) as Map<*, *>
    val questionOwner = fetched[questionOwnerKey] as Account
    val answerer = fetched[answererKey] as Account
    var question = fetched[questionKey] as Question

    val pendingAnswerer =
        Question.PendingAnswerer(userId = answerer.websafeId, username = answerer.username)
    question = question.copy(pendingAnswerers = question.pendingAnswerers.plus(pendingAnswerer))

    ofy().save().entity(question)

    val message = PushMessage(
        to = questionOwner.fcmToken,
        data = mapOf(
            "FCM_PAYLOAD_TYPE" to NotificationTypes.FCM_TYPE_COMMENTER_AVAILABLE,
            "FCM_PAYLOAD_ANSWERER_USERNAME" to answerer.username,
            "FCM_PAYLOAD_QUESTION_TRIMMED" to question.trimmedNotificationContent))

    notificationService.send(message)
  }
}