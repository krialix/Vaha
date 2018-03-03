package com.vaha.server.question.usecase

import com.googlecode.objectify.Key
import com.googlecode.objectify.Ref
import com.vaha.server.base.UseCase
import com.vaha.server.category.entity.Category
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.client.QuestionClient
import com.vaha.server.question.entity.Question
import com.vaha.server.user.entity.Account

class UpdateQuestionUseCase(
    private val ownerId: String?,
    private val questionId: String,
    private val content: String,
    private val categoryId: String
) : UseCase<QuestionClient> {

    override fun run(): QuestionClient {
        val questionKey = Key.create<Question>(questionId)
        val ownerKey = Key.create<Account>(ownerId)
        val categoryKey = Key.create<Category>(categoryId)

        val fetched = ofy().load().keys(questionKey, ownerKey) as Map<*, *>
        var question = fetched[questionKey] as Question
        val user = fetched[ownerKey] as Account

        content.let { question = question.copy(content = content) }
        categoryId.let { question = question.copy(category = Ref.create(categoryKey)) }

        ofy().save().entities(question, user)

        return QuestionClient.from(question = question, isOwner = true)
    }
}