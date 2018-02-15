package com.vaha.android.feature.base

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.RestoreViewOnCreateController
import com.bluelinelabs.conductor.archlifecycle.ControllerLifecycleRegistryOwner
import com.vaha.android.util.KeyboardUtil

abstract class BaseController : RestoreViewOnCreateController, LifecycleOwner {

    private val lifecycleRegistryOwner by lazy(LazyThreadSafetyMode.NONE) {
        ControllerLifecycleRegistryOwner(this)
    }

    private val inject by lazy(LazyThreadSafetyMode.NONE) { injectDependencies() }

    private var unbinder: Unbinder? = null

    protected constructor()

    protected constructor(args: Bundle) : super(args)

    override fun onContextAvailable(context: Context) {
        super.onContextAvailable(context)
        inject
    }

    protected abstract fun inflateView(inflater: LayoutInflater, container: ViewGroup): View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = inflateView(inflater, container)
        unbinder = ButterKnife.bind(this, view)
        onViewBound(view)
        return view
    }

    @CallSuper
    protected open fun onViewBound(view: View) {
    }

    protected open fun injectDependencies() {
    }

    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistryOwner.lifecycle

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        unbinder?.unbind()
    }

    protected val supportActionBar: ActionBar?
        get() {
            val actionBarInterface = activity as ActionBarProvider?
            return actionBarInterface?.getSupportActionBar()
        }

    protected fun setSupportActionBar(toolbar: Toolbar) {
        val actionBarInterface = activity as ActionBarProvider?
        actionBarInterface?.setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { router.handleBack() }
    }

    override fun handleBack(): Boolean {
        view?.let { KeyboardUtil.hideKeyboard(it) }
        return super.handleBack()
    }
}
