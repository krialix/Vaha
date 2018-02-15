package com.vaha.android.feature.base

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Toast
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.google.firebase.auth.FirebaseAuth
import com.vaha.android.R
import com.vaha.android.VahaApplication
import com.vaha.android.VahaService
import com.vaha.android.data.repository.SessionRepository
import com.vaha.android.feature.BottomNavigationController
import com.vaha.android.feature.auth.signin.SignInController
import com.vaha.android.feature.session.SessionController
import com.vaha.android.util.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ActionBarProvider {

    @Inject
    lateinit var sessionRepository: SessionRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var router: Router

    private val disposable: CompositeDisposable by lazy(LazyThreadSafetyMode.NONE) { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VahaApplication.appComponent.inject(this)
        setContentView(R.layout.activity_main)

        val controllerContainer = findViewById<ViewGroup>(R.id.controller_container)

        router = Conductor.attachRouter(this, controllerContainer, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser

        if (!router.hasRootController()) {
            val controller = when (user) {
                null -> SignInController.create()
                else -> BottomNavigationController.create()
            }

            router.setRoot(RouterTransaction.with(BottomNavigationController.create()))

            onNewIntent(intent)
        }

        //user?.let { checkUserIsInActiveSession() }

        startService(Intent(this, VahaService::class.java))
    }

    private fun checkUserIsInActiveSession() {
        sessionRepository.checkActiveSession()
            .filter { it.id != null }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.session_dialog_exists))
                    .setPositiveButton(getString(R.string.session_dialog_continue), { _, _ ->
                        val controller = SessionController.create(
                            questionId = it.id,
                            userId = sharedPreferences.getString("userId", ""),
                            username = sharedPreferences.getString("username", ""),
                            answererId = it.answererId,
                            questionOwner = it.owner,
                            enableWriting = true
                        )

                        router.pushController(RouterTransaction.with(controller))
                    })
                    .setNegativeButton(android.R.string.cancel, { _, _ ->

                    })
                    .show()
            }, Timber::e)
            .addTo(disposable)
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val type = intent?.getStringExtra("FCM_PAYLOAD_TYPE")

        when (type) {
            "1" -> startSessionRequest(intent)
            "2", "3" -> processSessionReady(intent)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancelAll()
    }

    private fun startSessionRequest(intent: Intent) {
        TODO("Navigate user to my questions list")
        /*val questionId = intent.getStringExtra("FCM_PAYLOAD_QUESTION_ID")
        val answererId = intent.getStringExtra("FCM_PAYLOAD_ANSWERER_ID")
        val questionOwnerId = intent.getStringExtra("FCM_PAYLOAD_QUESTION_OWNER_ID")

        sessionRepository.startSession(questionId, answererId)
            .subscribeOn(Schedulers.io())
            .retry(Predicate { it is SSLException })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
              sharedPreferences.edit().putString(KEY_ACTIVE_QUESTION_ID, questionId).apply()

              val localUserId = sharedPreferences.getString("userId", "")
              val targetUserId = if (localUserId == answererId) questionOwnerId else answererId

              val controller = SessionController.create(
                  questionId,
                  sharedPreferences.getString("userId", ""),
                  targetUserId,
                  sharedPreferences.getString("username", ""),
                  true,
                  true)
              router.pushController(RouterTransaction.with(controller))
            }, {
              Timber.e(it)
              showToast(R.string.notification_error_start_session)
            })
            .addTo(disposable)*/
    }

    private fun processSessionReady(intent: Intent) {
        val questionId = intent.getStringExtra("FCM_PAYLOAD_QUESTION_ID")
        val answererId = intent.getStringExtra("FCM_PAYLOAD_ANSWERER_ID")

        val controller = SessionController.create(
            questionId = questionId,
            userId = sharedPreferences.getString("userId", ""),
            username = sharedPreferences.getString("username", ""),
            answererId = answererId,
            questionOwner = false,
            enableWriting = true
        )

        router.pushController(RouterTransaction.with(controller))
    }

    private fun showToast(@StringRes stringRes: Int) {
        Toast.makeText(this, stringRes, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val KEY_ACTIVE_QUESTION_ID = "key_active_question_id"
    }
}
