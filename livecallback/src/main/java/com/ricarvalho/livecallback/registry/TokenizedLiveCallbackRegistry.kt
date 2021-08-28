package com.ricarvalho.livecallback.registry

import androidx.lifecycle.Lifecycle
import com.ricarvalho.livecallback.CallbackToken
import com.ricarvalho.livecallback.LiveCallbackContainer

@JvmInline
value class TokenizedLiveCallbackRegistry<I, O> private constructor (
    private val registry: MutableMap<CallbackToken<I, O>, LiveCallbackContainer<I, O>>
) : LiveCallbackRegistry<I, O>, (CallbackToken<I, O>, I) -> List<O> {
    constructor() : this(mutableMapOf())

    override fun register(lifecycle: Lifecycle, runWhileStopped: Boolean, callback: (I) -> O) =
        CallbackToken(callback).also { token ->
            val container = registry.getOrPut(token) {
                LiveCallbackContainer(whenAllBeDestroyed = { registry.remove(token) })
            }
            container.add(lifecycle, runWhileStopped, callback)
        }

    override fun invoke(token: CallbackToken<I, O>, input: I) =
        registry[token]?.invoke(input).orEmpty()
}