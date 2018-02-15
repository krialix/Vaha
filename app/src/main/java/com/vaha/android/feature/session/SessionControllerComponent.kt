package com.vaha.android.feature.session

import dagger.Subcomponent

@Subcomponent
interface SessionControllerComponent {

    fun inject(controller: SessionController)

    @Subcomponent.Builder
    interface Builder {
        fun build(): SessionControllerComponent
    }
}