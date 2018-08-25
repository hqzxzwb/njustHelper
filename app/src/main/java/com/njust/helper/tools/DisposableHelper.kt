package com.njust.helper.tools

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class DisposableHelper {
    private var compositeDisposable: CompositeDisposable? = null
    private var active = false

    fun activate() {
        active = true
    }

    fun deactivate() {
        active = false
        compositeDisposable?.dispose()
        compositeDisposable = null
    }

    fun add(d: Disposable) {
        if (active) {
            var container = compositeDisposable
            if (container == null) {
                container = CompositeDisposable()
                compositeDisposable = container
            }
            container.add(d)
        } else {
            d.dispose()
        }
    }
}

fun Disposable.addToDisposableHelper(disposableHelper: DisposableHelper) {
    disposableHelper.add(this)
}
