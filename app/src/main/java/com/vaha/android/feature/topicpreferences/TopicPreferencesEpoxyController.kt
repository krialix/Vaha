package com.vaha.android.feature.topicpreferences

import com.airbnb.epoxy.TypedEpoxyController
import com.vaha.android.data.entity.TopicResponse

internal class TopicPreferencesEpoxyController : TypedEpoxyController<List<TopicResponse>>() {

    lateinit var topicSubscribeListener: TopicSubscribeListener

    override fun buildModels(data: List<TopicResponse>) {
        for (client in data) {
            createTopicPrefModel(client)
        }
    }

    private fun createTopicPrefModel(client: TopicResponse) {
        TopicPrefViewModel_()
            .id(client.displayName)
            .topicResponse(client)
            .selectListener { _, subscribe ->
                when {
                    subscribe -> topicSubscribeListener.onTopicSubscribe(client.displayName)
                    else -> topicSubscribeListener.onTopicUnsubscribe(client.displayName)
                }
            }
            .addTo(this)
    }

    interface TopicSubscribeListener {
        fun onTopicSubscribe(topicName: String)

        fun onTopicUnsubscribe(topicName: String)
    }
}
