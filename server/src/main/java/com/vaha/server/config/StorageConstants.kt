package com.vaha.server.config

object StorageConstants {

  private val BASE_BUCKET = "vaha-dev.appspot.com"

  private val GS_PREFIX = "/gs"

  val SERVE_CATEGORIES_PATH = "$GS_PREFIX/$BASE_BUCKET/categories"

  val MINI_SIZE = 48
  val THUMB_SIZE = 150
  val LOW_SIZE = 340
  val MEDIUM_SIZE = 600
  val LARGE_SIZE = 800
}
