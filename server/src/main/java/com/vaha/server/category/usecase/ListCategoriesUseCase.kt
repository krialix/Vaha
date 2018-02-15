package com.vaha.server.category.usecase

import com.vaha.server.base.UseCase
import com.vaha.server.category.client.CategoryClient
import com.vaha.server.category.entity.Category
import com.vaha.server.ofy.OfyService.ofy

class ListCategoriesUseCase : UseCase<Collection<CategoryClient>> {

  override fun run(): Collection<CategoryClient> = ofy()
      .load()
      .type(Category::class.java)
      .iterable()
      .map { CategoryClient.from(it) }
}