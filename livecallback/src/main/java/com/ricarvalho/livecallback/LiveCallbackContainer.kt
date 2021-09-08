package com.ricarvalho.livecallback

import androidx.lifecycle.Lifecycle

internal class LiveCallbackContainer<I, O>(
    private val whenAllBeDestroyed: ((container: LiveCallbackContainer<I, O>) -> Unit)? = null
) : (I) -> List<O> {
    private val callbacks = mutableListOf<LiveCallback<I, O>>()

    fun add(lifecycle: Lifecycle, runWhileStopped: Boolean = false, callback: Callback<I, O>) {
        callbacks += LiveCallback(lifecycle, callback, runWhileStopped, whenDestroyed = {
            callbacks.remove(it)
            if (callbacks.isEmpty()) whenAllBeDestroyed?.invoke(this)
        })
    }

    override operator fun invoke(input: I) = callbacks.mapNotNull { it(input) }
}