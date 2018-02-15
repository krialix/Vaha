package com.vaha.android.ui.recyclerview

import android.support.annotation.CallSuper
import android.view.View
import butterknife.ButterKnife
import com.airbnb.epoxy.EpoxyHolder

open class BaseEpoxyHolder : EpoxyHolder() {

    protected lateinit var itemView: View

    @CallSuper
    override fun bindView(itemView: View) {
        this.itemView = itemView
        ButterKnife.bind(this, itemView)
    }
}
