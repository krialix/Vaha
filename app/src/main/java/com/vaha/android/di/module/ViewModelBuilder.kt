package com.vaha.android.di.module

import android.arch.lifecycle.ViewModelProvider
import com.vaha.android.di.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
internal abstract class ViewModelBuilder {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}