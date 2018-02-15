package com.vaha.android.components

import com.airbnb.epoxy.SimpleEpoxyModel
import com.vaha.android.R

class VerifyEmailModel : SimpleEpoxyModel(R.layout.view_verify_email_info) {
    override fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int = 2
}