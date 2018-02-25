package com.vaha.server.endpointsvalidator

import com.google.api.server.spi.ServiceException

interface Validator {

    fun isValid(): Boolean

    @Throws(ServiceException::class)
    fun onException()
}
