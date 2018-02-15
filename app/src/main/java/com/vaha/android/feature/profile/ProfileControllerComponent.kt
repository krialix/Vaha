package com.vaha.android.feature.profile

import dagger.Subcomponent

@Subcomponent
interface ProfileControllerComponent {

    fun inject(controller: ProfileController)

    @Subcomponent.Builder
    interface Builder {
        fun build(): ProfileControllerComponent
    }
}