package com.vaha.android.di.module

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.vaha.android.feature.ask.AskControllerComponent
import com.vaha.android.feature.auth.signin.SignInControllerComponent
import com.vaha.android.feature.auth.signup.SignUpControllerComponent
import com.vaha.android.feature.categorylist.CategoryListControllerComponent
import com.vaha.android.feature.profile.ProfileControllerComponent
import com.vaha.android.feature.questionlist.QuestionListControllerComponent
import com.vaha.android.feature.session.SessionControllerComponent
import com.vaha.android.feature.sessionlist.SessionListControllerComponent
import com.vaha.android.feature.topicpreferences.TopicPreferencesControllerComponent
import dagger.Module
import dagger.Provides

@Module(
    subcomponents = [
        SignInControllerComponent::class,
        AskControllerComponent::class,
        SignUpControllerComponent::class,
        CategoryListControllerComponent::class,
        ProfileControllerComponent::class,
        QuestionListControllerComponent::class,
        SessionControllerComponent::class,
        SessionListControllerComponent::class,
        TopicPreferencesControllerComponent::class
    ]
)
class AppModule {

    @Provides
    fun providesSharedPreferences(application: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }
}