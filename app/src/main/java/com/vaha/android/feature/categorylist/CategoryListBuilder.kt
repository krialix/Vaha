package com.vaha.android.feature.categorylist

import android.arch.lifecycle.ViewModel
import com.vaha.android.di.key.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class CategoryListBuilder {

    @Binds
    @IntoMap
    @ViewModelKey(CategoryListViewModel::class)
    abstract fun bindDiscoverViewModel(viewModel: CategoryListViewModel): ViewModel
}