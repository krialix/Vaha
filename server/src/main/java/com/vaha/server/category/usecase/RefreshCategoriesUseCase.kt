package com.vaha.server.category.usecase

import com.google.appengine.api.images.ImagesService
import com.google.appengine.api.images.ImagesServiceFactory
import com.vaha.server.base.UseCase
import com.vaha.server.category.entity.Category
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.util.CategoryUtil

class RefreshCategoriesUseCase : UseCase<Unit> {

  private val imagesService: ImagesService = ImagesServiceFactory.getImagesService()

  override fun run() {
    ofy()
        .load()
        .type(Category::class.java)
        .iterable()
        .map { it.copy(image = CategoryUtil.getImage(it.displayName, imagesService)) }
        .let { ofy().save().entities(it) }
  }
}