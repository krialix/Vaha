package com.vaha.server.question.usecase

import com.google.api.server.spi.response.BadRequestException
import com.googlecode.objectify.Key
import com.googlecode.objectify.Ref
import com.vaha.server.base.UseCase
import com.vaha.server.category.entity.Category
import com.vaha.server.notification.NotificationService
import com.vaha.server.notification.NotificationTypes
import com.vaha.server.notification.PushMessage
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.client.QuestionClient
import com.vaha.server.question.entity.Question
import com.vaha.server.user.entity.Account

class InsertQuestionUseCase(
    private val ownerId: String,
    private val content: String,
    private val categoryId: String,
    private val notificationService: NotificationService
) : UseCase<QuestionClient> {

  override fun run(): QuestionClient {
    val ownerKey = Key.create<Account>(ownerId)
    val categoryKey = Key.create<Category>(categoryId)

    val fetched = ofy().load().keys(ownerKey, categoryKey) as Map<*, *>
    var user = fetched[ownerKey] as Account
    var category = fetched[categoryKey] as Category

    if (user.dailyQuestionCount == 0) {
      throw BadRequestException("Daily question limit is over")
    }

    user = user.copy(questionCount = user.questionCount.inc(),
        dailyQuestionCount = user.dailyQuestionCount.dec())

    category = category.copy(questionCount = category.questionCount.inc())

    val question = Question(
        ownerKey,
        username = user.username,
        content = content,
        category = Ref.create(category),
        owner = true)

    ofy().save().entities(question, user, category)

    val data = mapOf(
        "FCM_PAYLOAD_USER_ID" to question.ownerWebsafeId,
        "FCM_PAYLOAD_TYPE" to NotificationTypes.FCM_TYPE_NEW_QUESTION,
        "FCM_PAYLOAD_CONTENT" to question.trimmedNotificationContent,
        "FCM_PAYLOAD_USERNAME" to user.username)

    val pushMessage = PushMessage("/topics/${category.topicName}", data = data)

    notificationService.send(pushMessage)

    return QuestionClient(question)
  }
}