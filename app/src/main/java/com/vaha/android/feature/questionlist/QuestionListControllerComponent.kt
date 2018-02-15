package com.vaha.android.feature.questionlist

import dagger.Subcomponent

@Subcomponent
interface QuestionListControllerComponent {

    fun inject(controller: QuestionListController)

    @Subcomponent.Builder
    interface Builder {
        fun build(): QuestionListControllerComponent
    }
}