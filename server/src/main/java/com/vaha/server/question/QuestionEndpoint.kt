package com.vaha.server.question

import com.google.api.server.spi.ServiceException
import com.google.api.server.spi.auth.common.User
import com.google.api.server.spi.config.ApiMethod
import com.google.api.server.spi.config.ApiMethod.HttpMethod.*
import com.google.api.server.spi.response.CollectionResponse
import com.vaha.server.base.BaseEndpoint
import com.vaha.server.endpointsvalidator.EndpointsValidator
import com.vaha.server.endpointsvalidator.validator.AuthValidator
import com.vaha.server.question.client.QuestionClient
import com.vaha.server.question.usecase.InsertQuestionUseCase
import com.vaha.server.question.usecase.ListAvailableQuestionsUseCase
import com.vaha.server.question.usecase.UpdateQuestionUseCase
import javax.annotation.Nullable
import javax.inject.Named

internal class QuestionEndpoint : BaseEndpoint() {

    @ApiMethod(name = "questions.insert", path = "questions", httpMethod = POST)
    @Throws(ServiceException::class)
    fun insert(
        @Named("content") content: String,
        @Named("categoryId") categoryId: String,
        user: User?
    ): QuestionClient {

        EndpointsValidator().on(AuthValidator.create(user))

        return InsertQuestionUseCase(user!!.id, content, categoryId).run()
    }

    @ApiMethod(name = "questions.update", path = "questions/{questionId}", httpMethod = PUT)
    @Throws(ServiceException::class)
    fun update(
        @Named("id") questionId: String,
        @Named("content") content: String,
        @Named("categoryId") categoryId: String,
        user: User?
    ): QuestionClient {

        EndpointsValidator().on(AuthValidator.create(user))

        return UpdateQuestionUseCase(user!!.id, questionId, content, categoryId).run()
    }

    @ApiMethod(name = "questions.list", path = "questions", httpMethod = GET)
    @Throws(ServiceException::class)
    fun list(
        @Nullable @Named("cursor") cursor: String?,
        @Named("sort") sort: ListAvailableQuestionsUseCase.QuestionSort,
        user: User?
    ): CollectionResponse<QuestionClient> {

        EndpointsValidator().on(AuthValidator.create(user))

        return ListAvailableQuestionsUseCase(cursor, user!!.id, sort).run()
    }
}