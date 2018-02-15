package com.vaha.android.feature.sessionlist

import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.vaha.android.components.SessionViewModel_
import com.vaha.android.ui.recyclerview.OnItemClickListener
import com.vaha.server.vahaApi.model.QuestionClient

internal class SessionListEpoxyController : TypedEpoxyController<List<QuestionClient>>() {

    private lateinit var onClickListener: OnItemClickListener<QuestionClient>

    override fun buildModels(data: List<QuestionClient>) {
        for (client in data) {
            createQuestionModel(client)
        }
    }

    private fun createQuestionModel(client: QuestionClient) {
        SessionViewModel_()
            .id(client.id)
            .session(client)
            .onClickListener(View.OnClickListener { onClickListener.onItemClick(it, client) })
            .addTo(this)
    }

    fun setOnClickListener(onClickListener: OnItemClickListener<QuestionClient>) {
        this.onClickListener = onClickListener
    }
}
