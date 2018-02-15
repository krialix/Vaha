package com.vaha.android.feature.categorylist

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.vaha.android.data.repository.CategoryRepository
import com.vaha.android.data.repository.UserRepository
import com.vaha.android.domain.CategoryListViewState
import com.vaha.android.domain.models.CategoryItem
import com.vaha.android.domain.models.CategoryListHeaderItem
import com.vaha.android.domain.models.EpoxyItem
import com.vaha.android.domain.models.VerifyEmailItem
import com.vaha.android.util.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

internal class CategoryListViewModel @Inject constructor(
    categoryRepository: CategoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val disposable = CompositeDisposable()

    val data = MutableLiveData<CategoryListViewState>()

    init {
        categoryRepository
            .listCategories()
            .map {
                val items = mutableListOf<EpoxyItem>()
                if (!userRepository.emailVerified()) {
                    items.add(VerifyEmailItem())
                }

                items.add(CategoryListHeaderItem())
                items.addAll(it.map { CategoryItem(it) }.toList())

                return@map items
            }
            .map { CategoryListViewState.Data(it) as CategoryListViewState }
            .onErrorReturn(CategoryListViewState::Error)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(data::setValue)
            .addTo(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
