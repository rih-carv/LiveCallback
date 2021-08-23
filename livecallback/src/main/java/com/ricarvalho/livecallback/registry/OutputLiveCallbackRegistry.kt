package com.ricarvalho.livecallback.registry

import androidx.lifecycle.Lifecycle
import com.ricarvalho.livecallback.OutputCallbackToken

interface OutputLiveCallbackRegistry<O> {
    fun register(lifecycle: Lifecycle, runWhileStopped: Boolean = false, callback: () -> O): OutputCallbackToken<O>
}