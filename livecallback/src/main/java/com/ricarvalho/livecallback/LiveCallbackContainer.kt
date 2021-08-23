package com.ricarvalho.livecallback

import androidx.lifecycle.Lifecycle

class LiveCallbackContainer<I, O> {
    private val callbacks = mutableListOf<LiveCallback<I, O>>()

    fun register(lifecycle: Lifecycle, callback: (I) -> O) {
        callbacks += LiveCallback(lifecycle, callback, whenDestroyed = {
            callbacks.remove(it)
        })
    }

    fun invoke(input: I) = callbacks.map { it.invoke(input) }
}