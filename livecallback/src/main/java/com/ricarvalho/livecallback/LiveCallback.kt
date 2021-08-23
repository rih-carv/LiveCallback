package com.ricarvalho.livecallback

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.LifecycleOwner

class LiveCallback<I, O>(
    lifecycle: Lifecycle,
    callback: (I) -> O,
    private val whenDestroyed: ((LiveCallback<I, O>) -> Unit)? = null
) : DefaultLifecycleObserver {
    private var lifecycle: Lifecycle? = lifecycle
    private var callback: ((I) -> O)? = callback

    init {
        if (lifecycle.currentState == DESTROYED) finish()
        else lifecycle.addObserver(this)
    }

    fun invoke(input: I) = callback?.invoke(input)

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        owner.lifecycle.removeObserver(this)
        finish()
    }

    private fun finish() {
        callback = null
        lifecycle = null
        whenDestroyed?.invoke(this)
    }
}