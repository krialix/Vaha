package com.vaha.android.data.repository

import com.vaha.android.data.api.VahaService
import com.vaha.android.data.entity.Category
import io.reactivex.Single
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val vahaService: VahaService) {

    fun listCategories(): Single<List<Category>> {
        return vahaService.listCategories()
    }
}
