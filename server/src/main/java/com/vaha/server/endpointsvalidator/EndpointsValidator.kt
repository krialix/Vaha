package com.vaha.server.endpointsvalidator

import com.google.api.server.spi.ServiceException

class EndpointsValidator {

  @Throws(ServiceException::class)
  fun on(validator: Validator): EndpointsValidator {
    if (!validator.isValid()) {
      validator.onException()
      return this
    }
    return this
  }
}
