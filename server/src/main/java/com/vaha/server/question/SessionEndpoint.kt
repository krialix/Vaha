package com.vaha.server.question

import com.google.api.client.http.HttpMethods.POST
import com.google.api.server.spi.ServiceException
import com.google.api.server.spi.auth.common.User
import com.google.api.server.spi.config.ApiMethod
import com.google.api.server.spi.config.ApiMethod.HttpMethod.GET
import com.vaha.server.base.BaseEndpoint
import com.vaha.server.endpointsvalidator.EndpointsValidator
import com.vaha.server.endpointsvalidator.validator.AuthValidator
import com.vaha.server.question.client.QuestionClient
import com.vaha.server.question.usecase.EndSessionUseCase
import com.vaha.server.question.usecase.ListSessionsUseCase
import com.vaha.server.question.usecase.SessionRequestUseCase
import com.vaha.server.question.usecase.StartSessionUseCase
import javax.inject.Named

internal class SessionEndpoint : BaseEndpoint() {

    @ApiMethod(name = "sessions.start", path = "sessions/start", httpMethod = POST)
    @Throws(ServiceException::class)
    fun start(
        @Named("questionId") questionId: String,
        @Named("answererId") answererId: String,
        user: User?
    ) {

        EndpointsValidator().on(AuthValidator.create(user))

        StartSessionUseCase(questionId, user?.id!!, answererId).run()
    }

    @ApiMethod(name = "sessions.end", path = "sessions/end", httpMethod = POST)
    @Throws(ServiceException::class)
    fun end(
        @Named("questionId") questionId: String,
        @Named("rating") rating: Int?,
        @Named("reasked") reasked: Boolean,
        user: User?
    ) {

        EndpointsValidator().on(AuthValidator.create(user))

        EndSessionUseCase(questionId, rating!!.toFloat(), reasked).run()
    }

    @ApiMethod(name = "sessions.list", path = "sessions", httpMethod = GET)
    @Throws(ServiceException::class)
    fun list(user: User?): Collection<QuestionClient> {

        EndpointsValidator().on(AuthValidator.create(user))

        return ListSessionsUseCase(user?.id!!).run()
    }

    @ApiMethod(name = "sessions.answererReady", path = "sessions/answererReady", httpMethod = POST)
    @Throws(ServiceException::class)
    fun answererReady(@Named("questionId") questionId: String, user: User?) {

        EndpointsValidator().on(AuthValidator.create(user))

        SessionRequestUseCase(user!!.id, questionId).run()
    }
}