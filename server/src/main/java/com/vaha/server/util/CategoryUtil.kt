package com.vaha.server.util

import com.google.appengine.api.images.ImagesService
import com.google.appengine.api.images.ServingUrlOptions

object CategoryUtil {

    private const val DUMMY_IMAGE_URL = "http://via.placeholder.com/250x250"
    private const val DEFAULT_BUCKET = "vahaapp-dev.appspot.com"

    @JvmStatic
    fun getImage(displayName: String, imagesService: ImagesService): String =
        when (ServerEnv.isDev()) {
            true -> DUMMY_IMAGE_URL
            false -> displayName
                .trim()
                .toLowerCase()
                .replace(" ", "_")
                .replace("&", "and")
                .let { "/gs/$DEFAULT_BUCKET/categories/$it.webp" }
                .let {
                    val options = ServingUrlOptions.Builder.withGoogleStorageFileName(it)

                    imagesService.getServingUrl(options)
                }
        }
}