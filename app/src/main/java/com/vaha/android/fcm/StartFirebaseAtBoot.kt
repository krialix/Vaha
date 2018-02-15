package com.vaha.android.fcm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vaha.android.VahaService

class StartFirebaseAtBoot : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.run {
            startService(Intent(FirebaseBackgroundService::class.java.name))
            startService(Intent(VahaService::class.java.name))
        }
    }
}