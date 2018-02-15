package com.vaha.android.feature.session

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.vaha.android.R
import com.vaha.android.VahaApplication
import com.vaha.android.data.entity.SessionMessage
import com.vaha.android.data.entity.chat.Message
import com.vaha.android.data.repository.SessionRepository
import com.vaha.android.fcm.FirebaseBackgroundService
import com.vaha.android.feature.base.BaseController
import com.vaha.android.util.BundleBuilder
import com.vaha.android.util.GlideApp
import com.vaha.android.util.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class SessionController(args: Bundle) : BaseController(args), MessageInput.InputListener,
    MessagesListAdapter.OnLoadMoreListener {

    @BindView(R.id.messagesList)
    lateinit var messagesList: MessagesList

    @BindView(R.id.input)
    lateinit var input: MessageInput

    @BindView(R.id.tv_session_end)
    lateinit var tvEndSession: TextView

    @Inject
    lateinit var sessionRepository: SessionRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var messagesListAdapter: MessagesListAdapter<SessionMessage>

    private lateinit var disposable: CompositeDisposable

    private lateinit var questionId: String
    private lateinit var userId: String
    private lateinit var answererId: String
    private lateinit var username: String

    private var questionOwner: Boolean = false
    private var enableWriting: Boolean = false

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View =
        inflater.inflate(R.layout.controller_session, container, false)

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        questionId = args.getString(KEY_QUESTION_ID)
        userId = args.getString(KEY_USER_ID)
        answererId = args.getString(KEY_ANSWERER_ID)
        username = args.getString(KEY_USERNAME)
        questionOwner = args.getBoolean(KEY_QUESTION_OWNER)

        enableWriting = args.getBoolean(KEY_ENABLE_WRITING)

        if (!questionOwner || !enableWriting) {
            tvEndSession.text = resources?.getString(R.string.session_finish)
        }

        disposable = CompositeDisposable()

        if (!enableWriting) {
            input.inputEditText.isEnabled = false
            input.button.isClickable = false
            input.inputEditText.hint = resources?.getString(R.string.session_disable_input)
        }

        setupAdapter()

        input.setInputListener(this)

        observeMessages()
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        activity?.stopService(Intent(activity, FirebaseBackgroundService::class.java))
        Timber.d("onAttach()")
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        Timber.d("onDetach()")
        activity?.startService(Intent(activity, FirebaseBackgroundService::class.java))
    }

    override fun injectDependencies() {
        super.injectDependencies()
        VahaApplication.appComponent.sessionComponent().build().inject(this)
    }

    private fun observeMessages() {
        sessionRepository.observeSessionMessages(questionId)
            .map { SessionMessage(it) }
            .subscribe({ messagesListAdapter.addToStart(it, true) }, Timber::e)
            .addTo(disposable)
    }

    override fun onSubmit(input: CharSequence): Boolean {
        if (input.isNotBlank()) {
            val message = Message(userId = userId, username = username, message = input.toString())

            sessionRepository.sendMessage(message, answererId, questionId)
        }

        return true
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int) {

    }

    private fun setupAdapter() {
        messagesListAdapter = MessagesListAdapter(userId, imageLoader)
        messagesListAdapter.setLoadMoreListener(this)

        messagesList.setAdapter(messagesListAdapter)
    }

    @OnClick(R.id.tv_session_end)
    fun endSession() {
        if (questionOwner && enableWriting) {
            showEndSessionDialog()
        } else {
            router.handleBack()
        }
    }

    private fun showEndSessionDialog() {
        val dialogView = View.inflate(activity, R.layout.view_rate_dialog, null)

        activity?.let {
            AlertDialog.Builder(it)
                .setView(dialogView)
                .setTitle(resources?.getString(R.string.session_dialog_title))
                .setPositiveButton(android.R.string.yes, { _, _ ->
                    val ratingBar = dialogView.findViewById<RatingBar>(R.id.rb_rate)
                    sessionRepository.endSession(questionId, false, ratingBar.numStars)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ router.handleBack() }, Timber::e)
                        .addTo(disposable)
                })
                .setNegativeButton(android.R.string.cancel, { _, _ -> Timber.d("Canceled!") })
                .setNeutralButton(resources?.getString(R.string.session_dialog_dispute), { _, _ ->
                    sessionRepository.endSession(questionId, true, -1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ router.handleBack() }, {
                            Timber.e(it)

                            if (it?.message?.contains("400")!!) {
                                view?.let {
                                    Snackbar.make(
                                        it,
                                        R.string.session_dialog_error_dispute,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        })
                        .addTo(disposable)
                })
                .show()
        }
    }

    companion object {
        const val KEY_SHOW_MESSAGE_NOTIFICATION = "KEY_SHOW_MESSAGE_NOTIFICATION"

        private const val KEY_QUESTION_ID = "KEY_QUESTION_ID"
        private const val KEY_USER_ID = "KEY_USER_ID"
        private const val KEY_ANSWERER_ID = "KEY_ANSWERER_ID"
        private const val KEY_USERNAME = "KEY_USERNAME"
        private const val KEY_QUESTION_OWNER = "KEY_QUESTION_OWNER"
        private const val KEY_ENABLE_WRITING = "KEY_ENABLE_WRITING"

        @JvmStatic
        fun create(
            questionId: String,
            userId: String,
            answererId: String,
            username: String,
            questionOwner: Boolean,
            enableWriting: Boolean
        ): SessionController =
            SessionController(
                BundleBuilder()
                    .putString(KEY_QUESTION_ID, questionId)
                    .putString(KEY_USER_ID, userId)
                    .putString(KEY_ANSWERER_ID, answererId)
                    .putString(KEY_USERNAME, username)
                    .putBoolean(KEY_QUESTION_OWNER, questionOwner)
                    .putBoolean(KEY_ENABLE_WRITING, enableWriting)
                    .build()
            )

        private val imageLoader = ImageLoader { imageView, url ->
            GlideApp.with(imageView).load(url).into(imageView)
        }
    }
}