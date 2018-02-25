package com.vaha.server.user.usecase

import com.google.api.server.spi.ServiceException
import com.googlecode.objectify.Key
import com.vaha.server.base.UseCase
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.user.entity.Account

class UpdateFcmTokenUseCase(
    private val fcmToken: String,
    private val userId: String
) : UseCase<Unit> {

    @Throws(ServiceException::class)
    override fun run() {
        var user = ofy().load().key(Key.create<Account>(userId)).now()

        user = user.copy(fcmToken = fcmToken)

        ofy().save().entity(user).now()
    }
}