package com.ricarvalho.livecallback.registry

import androidx.lifecycle.Lifecycle
import com.ricarvalho.livecallback.Callback
import com.ricarvalho.livecallback.CallbackToken

interface LiveCallbackRegistry<I, O> {
    fun register(
        lifecycle: Lifecycle,
        runWhileStopped: Boolean = false,
        callback: Callback<I, O>
    ): CallbackToken<I, O>
}