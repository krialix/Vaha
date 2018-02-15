package com.vaha.android.feature.categorylist

import dagger.Subcomponent

@Subcomponent
interface CategoryListControllerComponent {

    fun inject(controller: CategoryListController)

    @Subcomponent.Builder
    interface Builder {
        fun build(): CategoryListControllerComponent
    }
}