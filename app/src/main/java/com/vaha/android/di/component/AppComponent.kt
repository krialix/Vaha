package com.vaha.android.di.component

import android.app.Application
import com.vaha.android.VahaApplication
import com.vaha.android.VahaService
import com.vaha.android.di.module.AppModule
import com.vaha.android.di.module.NetModule
import com.vaha.android.di.module.ViewModelBuilder
import com.vaha.android.fcm.FirebaseBackgroundService
import com.vaha.android.fcm.VahaFirebaseInstanceIdService
import com.vaha.android.fcm.VahaFirebaseMessagingService
import com.vaha.android.feature.ask.AskControllerComponent
import com.vaha.android.feature.auth.signin.SignInControllerComponent
import com.vaha.android.feature.auth.signup.SignUpControllerComponent
import com.vaha.android.feature.base.MainActivity
import com.vaha.android.feature.categorylist.CategoryListBuilder
import com.vaha.android.feature.categorylist.CategoryListControllerComponent
import com.vaha.android.feature.profile.ProfileControllerComponent
import com.vaha.android.feature.questionlist.QuestionListControllerComponent
import com.vaha.android.feature.session.SessionControllerComponent
import com.vaha.android.feature.sessionlist.SessionListControllerComponent
import com.vaha.android.feature.topicpreferences.TopicPreferencesControllerComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ViewModelBuilder::class,
        CategoryListBuilder::class,
        AppModule::class,
        NetModule::class]
)
interface AppComponent {

    fun askBuilder(): AskControllerComponent.Builder

    fun signInControllerComponent(): SignInControllerComponent.Builder

    fun signUpControllerComponent(): SignUpControllerComponent.Builder

    fun categoryListComponent(): CategoryListControllerComponent.Builder

    fun profileComponent(): ProfileControllerComponent.Builder

    fun questionListComponent(): QuestionListControllerComponent.Builder

    fun sessionComponent(): SessionControllerComponent.Builder

    fun sessionListComponent(): SessionListControllerComponent.Builder

    fun topicPreferencesComponent(): TopicPreferencesControllerComponent.Builder


    fun inject(vahaApplication: VahaApplication)

    fun inject(vahaFirebaseInstanceIdService: VahaFirebaseInstanceIdService)

    fun inject(vahaFirebaseMessagingService: VahaFirebaseMessagingService)

    fun inject(mainActivity: MainActivity)

    fun inject(firebaseBackgroundService: FirebaseBackgroundService)

    fun inject(vahaService: VahaService)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}