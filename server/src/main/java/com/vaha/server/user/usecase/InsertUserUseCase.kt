package com.vaha.server.user.usecase

import com.google.api.server.spi.ServiceException
import com.google.api.server.spi.response.ConflictException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.googlecode.objectify.Work
import com.vaha.server.base.UseCase
import com.vaha.server.category.entity.Category
import com.vaha.server.notification.NotificationService
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.user.UserJsonPayload
import com.vaha.server.user.client.UserClient
import com.vaha.server.user.entity.Account

class InsertUserUseCase(
    private val base64Payload: String,
    private val notificationService: NotificationService
) : UseCase<UserClient> {

  @Throws(ServiceException::class)
  override fun run(): UserClient {
    val payload = UserJsonPayload.from(base64Payload)

    assertUserIsNotRegistered(payload.email, payload.username)

    val user = createUserFromPayload(payload)

    val createRequest = createUserRequest(payload, user)

    return ofy().transact(Work {
      ofy().save().entities(user).now()

      FirebaseAuth.getInstance().createUserAsync(createRequest).get()

      subscribeToCategories(user.fcmToken)

      return@Work UserClient.from(user)
    })
  }

  private fun subscribeToCategories(fcmToken: String) {
    val categories = ofy().transactionless().load().type(Category::class.java).iterable()
    categories.forEach { notificationService.subscribeTopic(fcmToken, it.topicName) }
  }

  private fun createUserFromPayload(payload: UserJsonPayload): Account =
      Account(username = payload.username, email = payload.email, fcmToken = payload.fcmToken)

  @Throws(ConflictException::class)
  private fun assertUserIsNotRegistered(email: String, username: String) {
    val emailExists = ofy()
        .load()
        .type(Account::class.java)
        .filter(Account.FIELD_EMAIL, email)
        .keys()
        .first()
        .now() != null

    val usernameExists = ofy()
        .load()
        .type(Account::class.java)
        .filter(Account.FIELD_USERNAME, username)
        .keys()
        .first()
        .now() != null

    if (emailExists) {
      throw ConflictException("email is in use.")
    }

    if (usernameExists) {
      throw ConflictException("username is in use.")
    }
  }

  private fun createUserRequest(
      payload: UserJsonPayload,
      account: Account
  ): UserRecord.CreateRequest =
      UserRecord.CreateRequest()
          .setUid(account.websafeId)
          .setEmail(account.email)
          .setDisplayName(account.username)
          .setPassword(payload.password)
}