package com.vaha.android.domain.models

abstract class ParamEpoxyItem<out M> : EpoxyItem() {

    /**
     * Gets item.
     *
     * @return the item
     */
    abstract fun item(): M
}