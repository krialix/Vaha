package com.vaha.server.category.usecase

import com.google.appengine.api.images.ImagesServiceFactory
import com.vaha.server.base.UseCase
import com.vaha.server.category.client.CategoryClient
import com.vaha.server.category.entity.Category
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.util.CategoryUtil
import com.vaha.server.util.ServerEnv

class InsertCategoryUseCase(
    private val displayName: String
) : UseCase<CategoryClient> {

    override fun run(): CategoryClient {
        val category = Category(
            displayName = displayName,
            image = CategoryUtil.getImage(displayName, ImagesServiceFactory.getImagesService())
        )
        val result = ofy().save().entity(category)
        if (ServerEnv.isTest()) {
            result.now()
        }

        return CategoryClient.from(category)
    }
}