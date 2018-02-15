package com.vaha.android.feature.auth.signup

import dagger.Subcomponent

@Subcomponent
interface SignUpControllerComponent {

    fun inject(controller: SignUpController)

    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpControllerComponent
    }
}