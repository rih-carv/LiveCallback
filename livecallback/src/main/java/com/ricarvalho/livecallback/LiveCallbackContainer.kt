package com.ricarvalho.livecallback

import androidx.lifecycle.Lifecycle

class LiveCallbackContainer<I, O>(
    private val whenAllBeDestroyed: ((LiveCallbackContainer<I, O>) -> Unit)? = null
) {
    private val callbacks = mutableListOf<LiveCallback<I, O>>()

    fun register(lifecycle: Lifecycle, callback: (I) -> O) {
        callbacks += LiveCallback(lifecycle, callback, whenDestroyed = {
            callbacks.remove(it)
            if (callbacks.isEmpty()) whenAllBeDestroyed?.invoke(this)
        })
    }

    fun invoke(input: I) = callbacks.mapNotNull { it.invoke(input) }
}