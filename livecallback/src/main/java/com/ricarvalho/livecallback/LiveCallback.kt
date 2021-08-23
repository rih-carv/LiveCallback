package com.ricarvalho.livecallback

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

class LiveCallback<I, O>(
    private val lifecycle: Lifecycle,
    private var callback: ((I) -> O)?
) : DefaultLifecycleObserver {
    init {
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) callback = null
        else lifecycle.addObserver(this)
    }

    fun invoke(input: I) = callback?.invoke(input)

    override fun onDestroy(owner: LifecycleOwner) {
        callback = null
        lifecycle.removeObserver(this)
    }
}