package com.vaha.android.feature.sessionlist

import dagger.Subcomponent

@Subcomponent
interface SessionListControllerComponent {

    fun inject(controller: SessionListController)

    @Subcomponent.Builder
    interface Builder {
        fun build(): SessionListControllerComponent
    }
}