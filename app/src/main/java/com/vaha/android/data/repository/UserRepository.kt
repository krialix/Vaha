package com.vaha.android.data.repository

import android.content.SharedPreferences
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.vaha.android.data.api.VahaService
import com.vaha.android.data.entity.RegisterPayload
import com.vaha.android.data.entity.User
import com.vaha.android.util.toSingle
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val vahaService: VahaService,
    private val sharedPreferences: SharedPreferences
) {

    fun getMe(): Single<User> {
        return vahaService.getMe()
    }

    fun registerUser(payload: RegisterPayload): Completable {
        return payload
            .convertToBase64String()
            .toSingle()
            .flatMap { vahaService.registerUser(it) }
            .doOnSuccess {
                sharedPreferences.edit()
                    .putString("userId", it.id)
                    .putString("username", it.username)
                    .apply()
            }
            .toCompletable()
    }

    fun emailVerified(): Boolean {
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.reload()?.let { Tasks.await(it) }
        return auth.currentUser?.isEmailVerified ?: false
    }

    fun updateFcmToken(token: String): Completable {
        return vahaService.updateFcmToken(token)
    }
}
