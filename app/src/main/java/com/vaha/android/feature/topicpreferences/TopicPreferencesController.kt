package com.vaha.android.feature.topicpreferences

import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import com.google.firebase.messaging.FirebaseMessaging
import com.vaha.android.R
import com.vaha.android.VahaApplication
import com.vaha.android.data.repository.UserRepository
import com.vaha.android.feature.base.BaseController
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class TopicPreferencesController : BaseController(),
    TopicPreferencesEpoxyController.TopicSubscribeListener {

    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var epoxyController: TopicPreferencesEpoxyController

    private val disposable = CompositeDisposable()

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View =
        inflater.inflate(R.layout.controller_topic_preferences, container, false)

    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setupRecyclerView()

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }

        /*userRepository.listFcmTopics()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ epoxyController.setData(it.items) }, Timber::e)
            .addTo(disposable)*/
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        disposable.clear()
    }

    override fun injectDependencies() {
        super.injectDependencies()
        VahaApplication.appComponent.topicPreferencesComponent().build().inject(this)
    }

    private fun setupRecyclerView() {
        epoxyController = TopicPreferencesEpoxyController()
        epoxyController.topicSubscribeListener = this

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity!!,
                DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = epoxyController.adapter
    }

    override fun onTopicSubscribe(topicName: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topicName)
    }

    override fun onTopicUnsubscribe(topicName: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName)
    }
}