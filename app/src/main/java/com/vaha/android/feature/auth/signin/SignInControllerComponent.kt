package com.vaha.android.feature.auth.signin

import dagger.Subcomponent

@Subcomponent
interface SignInControllerComponent {

    fun inject(controller: SignInController)

    @Subcomponent.Builder
    interface Builder {
        fun build(): SignInControllerComponent
    }
}