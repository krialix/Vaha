package com.vaha.android

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.annotation.StringRes
import android.widget.Toast
import com.vaha.android.data.repository.SessionRepository
import com.vaha.android.util.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.net.ssl.SSLException

class VahaService : Service() {

    @Inject
    lateinit var sessionRepository: SessionRepository

    private val disposable = CompositeDisposable()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        VahaApplication.appComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val type = intent?.getStringExtra("FCM_PAYLOAD_TYPE")
        Timber.d("onNewIntent(): %s", type)

        when (type) {
            "0", "5" -> sendAnswererAvailableRequest(intent)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun sendAnswererAvailableRequest(intent: Intent) {
        val questionId = intent.getStringExtra("FCM_PAYLOAD_QUESTION_ID")

        sessionRepository.sendRequest(questionId)
            .subscribeOn(Schedulers.io())
            .retry(Predicate { it is SSLException })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {
                Timber.e(it)

                if (it.message!!.contains("Only question owner can start a session")) {
                    showToast(R.string.notification_error_active_session)
                } else {
                    showToast(R.string.notification_error_start_session)
                }
            })
            .addTo(disposable)
    }

    private fun showToast(@StringRes stringRes: Int) {
        Toast.makeText(this, stringRes, Toast.LENGTH_LONG).show()
    }
}