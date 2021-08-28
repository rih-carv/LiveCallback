package com.ricarvalho.livecallback

import androidx.lifecycle.Lifecycle

internal class LiveCallbackContainer<I, O>(
    private val whenAllBeDestroyed: ((LiveCallbackContainer<I, O>) -> Unit)? = null
) : (I) -> List<O> {
    private val callbacks = mutableListOf<LiveCallback<I, O>>()

    fun register(lifecycle: Lifecycle, runWhileStopped: Boolean = false, callback: (I) -> O) {
        callbacks += LiveCallback(lifecycle, callback, runWhileStopped, whenDestroyed = {
            callbacks.remove(it)
            if (callbacks.isEmpty()) whenAllBeDestroyed?.invoke(this)
        })
    }

    override fun invoke(input: I) = callbacks.mapNotNull { it(input) }
}