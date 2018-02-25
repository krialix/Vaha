package com.vaha.server.question.usecase

import com.google.api.server.spi.response.BadRequestException
import com.googlecode.objectify.Key
import com.googlecode.objectify.Ref
import com.vaha.server.base.UseCase
import com.vaha.server.category.entity.Category
import com.vaha.server.event.NewQuestionEvent
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.client.QuestionClient
import com.vaha.server.question.entity.Question
import com.vaha.server.user.entity.Account

class InsertQuestionUseCase(
    private val ownerId: String,
    private val content: String,
    private val categoryId: String
) : UseCase<QuestionClient> {

    override fun run(): QuestionClient {
        val ownerKey = Key.create<Account>(ownerId)
        val categoryKey = Key.create<Category>(categoryId)

        val fetched = ofy().load().keys(ownerKey, categoryKey) as Map<*, *>
        val user = fetched[ownerKey] as Account
        val category = fetched[categoryKey] as Category

        if (user.isDailyLimitZero) {
            throw BadRequestException("Daily question limit is over")
        }

        user.questionCount.inc()
        user.dailyQuestionCount.dec()
        category.questionCount.inc()

        val question = Question(
            parent = ownerKey,
            username = user.username,
            content = content,
            category = Ref.create(category),
            owner = true
        )

        ofy().save().entities(question, user, category)

        NewQuestionEvent(
            user.websafeId,
            user.username,
            question.trimmedNotificationContent,
            category.topicName
        )

        return QuestionClient(question)
    }
}