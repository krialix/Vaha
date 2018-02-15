package com.vaha.android.feature.topicpreferences

import dagger.Subcomponent

@Subcomponent
interface TopicPreferencesControllerComponent {

    fun inject(controller: TopicPreferencesController)

    @Subcomponent.Builder
    interface Builder {
        fun build(): TopicPreferencesControllerComponent
    }
}