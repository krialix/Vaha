package com.vaha.android.feature.questionlist

import com.airbnb.epoxy.TypedEpoxyController
import com.vaha.android.components.QuestionView
import com.vaha.android.components.QuestionViewModel_
import com.vaha.android.data.entity.Question
import com.vaha.android.ui.recyclerview.OnItemClickListener

internal class QuestionListEpoxyController : TypedEpoxyController<List<Question>>() {

    private lateinit var onStartSessionClickListener: OnItemClickListener<Question>

    override fun buildModels(data: List<Question>) {
        data.forEach { createQuestionModel(it) }
    }

    fun setOnStartSessionClickListener(onStartSessionClickListener: OnItemClickListener<Question>) {
        this.onStartSessionClickListener = onStartSessionClickListener
    }

    private fun createQuestionModel(client: Question) {
        QuestionViewModel_()
            .id(client.id)
            .isOwner(client.user.isOwner)
            .categoryName(client.category.nameEn)
            .askedBy(client.user.displayName)
            .answeredBy(client.answerer?.displayName)
            .content(client.content)
            .sendRequestVisibility(client.isRequestSent)
            .onStartSessionClickListener { view ->
                onStartSessionClickListener.onItemClick(view, client)
            }
            .pendingRequests(client.requests?.map {
                QuestionView.PendingRequest(
                    it.userId,
                    it.displayName,
                    it.rating
                )
            })
            .addTo(this)
    }
}
