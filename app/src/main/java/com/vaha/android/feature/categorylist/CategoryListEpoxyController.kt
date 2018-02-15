package com.vaha.android.feature.categorylist

import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.vaha.android.components.CategoryGridViewModel_
import com.vaha.android.components.CategoryListHeaderModel
import com.vaha.android.components.VerifyEmailModel
import com.vaha.android.data.entity.Category
import com.vaha.android.domain.models.CategoryItem
import com.vaha.android.domain.models.CategoryListHeaderItem
import com.vaha.android.domain.models.EpoxyItem
import com.vaha.android.domain.models.VerifyEmailItem
import com.vaha.android.ui.recyclerview.OnItemClickListener

internal class CategoryListEpoxyController : TypedEpoxyController<List<EpoxyItem>>() {

    lateinit var onItemClickListener: OnItemClickListener<Category>
    lateinit var onVerifyEmailClickListener: View.OnClickListener

    override fun buildModels(data: List<EpoxyItem>) = data.forEach {
        when (it) {
            is VerifyEmailItem -> VerifyEmailModel().onClick(onVerifyEmailClickListener).id(it.id()).addTo(
                this
            )
            is CategoryListHeaderItem -> CategoryListHeaderModel().id(it.id()).addTo(this)
            is CategoryItem -> createGroupGridModel(it.item())
        }
    }

    private fun createGroupGridModel(entity: Category) {
        CategoryGridViewModel_()
            .id(entity.id)
            .category(entity)
            .onClickListener(View.OnClickListener { onItemClickListener.onItemClick(it, entity) })
            .spanSizeOverride({ totalSpanCount, _, _ -> totalSpanCount / 2 })
            .addTo(this)
    }
}
