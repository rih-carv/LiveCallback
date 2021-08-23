package com.ricarvalho.livecallback.registry

import androidx.lifecycle.Lifecycle
import com.ricarvalho.livecallback.CallbackToken
import com.ricarvalho.livecallback.LiveCallbackContainer
import com.ricarvalho.livecallback.OutputCallbackToken

@JvmInline
value class TokenizedOutputLiveCallbackRegistry<O> private constructor (
    private val registry: MutableMap<OutputCallbackToken<O>, LiveCallbackContainer<Any?, O>>
) : OutputLiveCallbackRegistry<O>, (OutputCallbackToken<O>) -> List<O> {
    constructor() : this(mutableMapOf())

    override fun register(lifecycle: Lifecycle, runWhileStopped: Boolean, callback: () -> O) =
        CallbackToken.output(callback).also { token ->
            val container = registry.getOrPut(token) {
                LiveCallbackContainer(whenAllBeDestroyed = { registry.remove(token) })
            }
            container.register(lifecycle, runWhileStopped) { callback() }
        }

    override fun invoke(token: OutputCallbackToken<O>) =
        registry[token]?.invoke(null).orEmpty()
}