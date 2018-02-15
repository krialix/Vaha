package com.vaha.android

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.google.firebase.FirebaseApp
import com.vaha.android.di.component.AppComponent
import com.vaha.android.di.component.DaggerAppComponent
import com.vaha.android.log.ReleaseTree
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class VahaApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        Fabric.with(
            this,
            Crashlytics.Builder()
                .core(
                    CrashlyticsCore.Builder()
                        .disabled(BuildConfig.DEBUG)
                        .build()
                )
                .build()
        )

        initTimber()

        appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)
    }

    fun initTimber() {
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else ReleaseTree())
    }

    companion object {
        @JvmStatic
        lateinit var appComponent: AppComponent
            private set
    }
}
