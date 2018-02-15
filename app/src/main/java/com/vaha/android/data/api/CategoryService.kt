package com.vaha.android.data.api

import com.vaha.server.vahaApi.VahaApi
import com.vaha.server.vahaApi.model.CategoryClientCollection
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryService @Inject constructor(private val vahaApi: VahaApi) {

    fun listCategories(): Single<CategoryClientCollection> =
        Single.fromCallable { vahaApi.categories().list().execute() }
}