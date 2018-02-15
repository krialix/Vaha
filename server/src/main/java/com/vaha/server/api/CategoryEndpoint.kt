package com.vaha.server.api

import com.google.api.server.spi.ServiceException
import com.google.api.server.spi.auth.common.User
import com.google.api.server.spi.config.ApiMethod
import com.google.api.server.spi.config.ApiMethod.HttpMethod.GET
import com.vaha.server.category.client.CategoryClient
import com.vaha.server.category.usecase.ListCategoriesUseCase
import com.vaha.server.endpointsvalidator.EndpointsValidator
import com.vaha.server.endpointsvalidator.validator.AuthValidator

internal class CategoryEndpoint : BaseEndpoint() {

  @ApiMethod(name = "categories.list", path = "categories", httpMethod = GET)
  @Throws(ServiceException::class)
  fun list(user: User?): Collection<CategoryClient> {

    EndpointsValidator().on(AuthValidator.create(user))

    return ListCategoriesUseCase().run()
  }
}