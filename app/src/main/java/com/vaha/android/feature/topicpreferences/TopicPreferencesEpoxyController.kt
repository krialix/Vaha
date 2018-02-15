package com.vaha.android.feature.topicpreferences

import com.airbnb.epoxy.TypedEpoxyController
import com.vaha.server.vahaApi.model.TopicResponse

internal class TopicPreferencesEpoxyController : TypedEpoxyController<List<TopicResponse>>() {

    lateinit var topicSubscribeListener: TopicSubscribeListener

    override fun buildModels(data: List<TopicResponse>) {
        for (client in data) {
            createTopicPrefModel(client)
        }
    }

    private fun createTopicPrefModel(client: TopicResponse) {
        TopicPrefViewModel_()
            .id(client.topicName)
            .topicResponse(client)
            .selectListener { _, subscribe ->
                when {
                    subscribe -> topicSubscribeListener.onTopicSubscribe(client.topicName)
                    else -> topicSubscribeListener.onTopicUnsubscribe(client.topicName)
                }
            }
            .addTo(this)
    }

    interface TopicSubscribeListener {
        fun onTopicSubscribe(topicName: String)

        fun onTopicUnsubscribe(topicName: String)
    }
}
