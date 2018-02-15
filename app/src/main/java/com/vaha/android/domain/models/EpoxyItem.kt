package com.vaha.android.domain.models

abstract class EpoxyItem {

    /**
     * Id string.
     *
     * @return the string
     */
    abstract fun id(): String

    /**
     * Gets weight.
     *
     * @return the weight
     */
    fun weight(): Float = 0f
}
