package com.vaha.android.feature.ask

import com.vaha.android.di.scope.ControllerScope
import dagger.Subcomponent

@ControllerScope
@Subcomponent
interface AskControllerComponent {

    fun inject(controller: AskController)

    @Subcomponent.Builder
    interface Builder {
        fun build(): AskControllerComponent
    }
}