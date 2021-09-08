package com.ricarvalho.livecallback.registry

import androidx.lifecycle.Lifecycle
import com.ricarvalho.livecallback.SimpleCallback
import com.ricarvalho.livecallback.SimpleCallbackToken

@JvmInline
value class TokenizedSimpleLiveCallbackRegistry private constructor (
    private val registry: TokenizedOutputLiveCallbackRegistry<Unit>
) : SimpleLiveCallbackRegistry, (SimpleCallbackToken) -> Unit {
    constructor() : this(TokenizedOutputLiveCallbackRegistry())

    override fun register(
        lifecycle: Lifecycle,
        runWhileStopped: Boolean,
        callback: SimpleCallback
    ) = registry.register(lifecycle, runWhileStopped, callback)

    override operator fun invoke(token: SimpleCallbackToken) {
        registry(token)
    }
}