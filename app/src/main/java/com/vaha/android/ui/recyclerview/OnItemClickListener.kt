package com.vaha.android.ui.recyclerview

import android.view.View

interface OnItemClickListener<in M> {

    fun onItemClick(v: View, m: M)
}
