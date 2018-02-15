package com.vaha.android.fcm

import android.content.SharedPreferences
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.vaha.android.VahaApplication
import timber.log.Timber
import javax.inject.Inject

/** This service do the unique task ; save FCM Id token locally & remotely  */
class VahaFirebaseInstanceIdService : FirebaseInstanceIdService() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        VahaApplication.appComponent.inject(this)
        super.onCreate()
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security from the previous token
     * had been compromised. Note that this is called when the InstanceID token is initially generated
     * so this is where you would retrieve the token.
     */
    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token

        Timber.d("onTokenRefresh(): %s", refreshedToken)

        refreshedToken?.let { sharedPreferences.edit().putString("fcmToken", it).apply() }
    }
}
