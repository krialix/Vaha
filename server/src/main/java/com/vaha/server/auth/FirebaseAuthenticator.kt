package com.vaha.server.auth

import com.google.api.server.spi.auth.common.User
import com.google.api.server.spi.config.Authenticator
import com.google.api.server.spi.config.Singleton
import com.google.firebase.auth.FirebaseAuth
import com.googlecode.objectify.Key
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.user.entity.Account
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest

@Singleton
class FirebaseAuthenticator : Authenticator {

  override fun authenticate(request: HttpServletRequest): User? {
    val authHeader = request.getHeader(OAuth2.HeaderType.AUTHORIZATION)

    if (authHeader.isNotBlank() && authHeader.contains(OAuth2.OAUTH_HEADER_NAME)) {
      val idToken = authHeader.replace("Bearer ", "").trim()

      val firebaseToken = FirebaseAuth.getInstance().verifyIdTokenAsync(idToken).get()

      val accountKey = findAccountKeyByEmail(firebaseToken.email)

      return User(accountKey?.toWebSafeString(), firebaseToken.email)
    }

    return null
  }

  companion object {
    private fun findAccountKeyByEmail(email: String): Key<Account>? =
        ofy().load()
            .type(Account::class.java)
            .filter(Account.FIELD_EMAIL, email)
            .keys()
            .first()
            .now()

    private val logger = Logger.getLogger(FirebaseAuthenticator::class.java.name)
  }
}
