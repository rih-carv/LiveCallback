package com.ricarvalho.livecallback

import androidx.lifecycle.Lifecycle

@JvmInline
value class TokenizedLiveCallbackRegistry<I, O> private constructor (
    private val registry: MutableMap<CallbackToken<I, O>, LiveCallbackContainer<I, O>>
) : (CallbackToken<I, O>, I) -> List<O> {
    constructor() : this(mutableMapOf())

    fun register(lifecycle: Lifecycle, runWhileStopped: Boolean = false, callback: (I) -> O) =
        CallbackToken(callback).also { token ->
            val container = registry.getOrPut(token) {
                LiveCallbackContainer(whenAllBeDestroyed = { registry.remove(token) })
            }
            container.register(lifecycle, runWhileStopped, callback)
        }

    override fun invoke(token: CallbackToken<I, O>, input: I) =
        registry[token]?.invoke(input).orEmpty()
}