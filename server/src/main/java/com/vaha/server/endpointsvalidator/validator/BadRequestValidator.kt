package com.vaha.server.endpointsvalidator.validator

import com.google.api.server.spi.ServiceException
import com.google.api.server.spi.response.BadRequestException
import com.google.common.base.Strings
import com.vaha.server.endpointsvalidator.Validator

class BadRequestValidator private constructor(
    private val any: Any?,
    private val message: String
) : Validator {

  override fun isValid(): Boolean {
    return if (any is String) {
      !Strings.isNullOrEmpty(any as String?)
    } else {
      any != null
    }
  }

  @Throws(ServiceException::class)
  override fun onException() {
    throw BadRequestException(message)
  }

  companion object {
    fun create(any: Any?, message: String): BadRequestValidator = BadRequestValidator(any, message)
  }
}
