package com.vaha.server.user.usecase

import com.google.api.server.spi.ServiceException
import com.googlecode.objectify.Key
import com.vaha.server.base.UseCase
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.user.client.UserClient
import com.vaha.server.user.entity.Account

class GetUserUseCase(private val userId: String?) : UseCase<UserClient> {

  @Throws(ServiceException::class)
  override fun run(): UserClient {
    val userKey = Key.create<Account>(userId)
    return UserClient.from(ofy().load().key(userKey).now())
  }
}