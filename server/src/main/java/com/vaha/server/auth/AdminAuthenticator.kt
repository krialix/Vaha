package com.vaha.server.auth

import com.google.api.server.spi.auth.common.User
import com.google.api.server.spi.config.Authenticator
import com.google.api.server.spi.config.Singleton
import com.vaha.server.config.Config
import javax.servlet.http.HttpServletRequest

@Singleton
class AdminAuthenticator : Authenticator {

    override fun authenticate(request: HttpServletRequest): User? {
        val authHeader = request.getHeader(OAuth2.HeaderType.AUTHORIZATION)

        authHeader?.let {
            if (it.split(" ").last() == Config.ADMIN_EMAIL) {
                return User("1", Config.ADMIN_EMAIL)
            }
        }

        return null
    }
}
