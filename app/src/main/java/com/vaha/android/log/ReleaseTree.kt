package com.vaha.android.log

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber

internal class ReleaseTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority)
        Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag)
        Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message)

        when (t) {
            null -> Crashlytics.logException(Exception(message))
            else -> Crashlytics.logException(t)
        }
    }

    override fun isLoggable(tag: String?, priority: Int): Boolean = priority >= Log.ERROR


    companion object {
        private const val CRASHLYTICS_KEY_PRIORITY = "priority"
        private const val CRASHLYTICS_KEY_TAG = "tag"
        private const val CRASHLYTICS_KEY_MESSAGE = "message"
    }
}
