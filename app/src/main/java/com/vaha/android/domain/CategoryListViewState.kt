package com.vaha.android.domain

import com.vaha.android.domain.models.EpoxyItem

sealed class CategoryListViewState {

    object Loading : CategoryListViewState()

    data class Error(val error: Throwable) : CategoryListViewState()

    data class Data(val epoxyItems: List<EpoxyItem>) : CategoryListViewState()
}