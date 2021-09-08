package com.ricarvalho.livecallback.registry

import androidx.lifecycle.Lifecycle
import com.ricarvalho.livecallback.InputCallback
import com.ricarvalho.livecallback.InputCallbackToken

@JvmInline
value class TokenizedInputLiveCallbackRegistry<I> private constructor (
    private val registry: TokenizedLiveCallbackRegistry<I, Unit>
) : InputLiveCallbackRegistry<I>, (InputCallbackToken<I>, I) -> Unit {
    constructor() : this(TokenizedLiveCallbackRegistry())

    override fun register(
        lifecycle: Lifecycle,
        runWhileStopped: Boolean,
        callback: InputCallback<I>
    ) = registry.register(lifecycle, runWhileStopped, callback)

    override operator fun invoke(token: InputCallbackToken<I>, input: I) {
        registry(token, input)
    }
}