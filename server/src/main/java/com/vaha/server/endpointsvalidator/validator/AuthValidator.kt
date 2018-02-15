package com.vaha.server.endpointsvalidator.validator

import com.google.api.server.spi.ServiceException
import com.google.api.server.spi.auth.common.User
import com.google.api.server.spi.response.UnauthorizedException
import com.vaha.server.endpointsvalidator.Validator

class AuthValidator private constructor(private val user: User?) : Validator {

  override fun isValid(): Boolean = !(user == null || user.id == null)

  @Throws(ServiceException::class)
  override fun onException() {
    throw UnauthorizedException("Only authenticated users may invoke this operation.")
  }

  companion object {
    fun create(user: User?): AuthValidator = AuthValidator(user)
  }
}
