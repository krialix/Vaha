package com.vaha.server.base

import com.google.api.server.spi.ServiceException

interface UseCase<out T> {

    @Throws(ServiceException::class)
    fun run(): T
}
