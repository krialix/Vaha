package com.vaha.android.util

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun <T : Any> T.toSingle(): Single<T> = Single.fromCallable { this }
fun Void.toCompletable(): Completable = Completable.fromAction { }

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable =
    apply { compositeDisposable.add(this) }
