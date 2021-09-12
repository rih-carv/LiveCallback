package com.ricarvalho.livecallback.registry

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.LifecycleOwner
import com.ricarvalho.livecallback.Callback
import com.ricarvalho.livecallback.CallbackToken

/**
 * A lifecycle-aware registry of callbacks that correlates callbacks and its origins.
 *
 * This is expected to be used with callbacks that receive a parameter and return a value.
 *
 * It should respect the lifecycle of other app components, such as activities, fragments, or
 * services. This awareness ensure the registered callbacks should be only invoked when appropriate.
 *
 * You can register a callback paired with an object that implements the [LifecycleOwner] interface.
 * This relationship allows the callback to be released when the state of the lifecycle changes to
 * [DESTROYED]. This is especially useful for activities and fragments because they can safely
 * register callbacks and not worry about leaks: the callback and lifecycle are expected to be
 * instantly released when their lifecycles are destroyed.
 *
 * @param[I] The type of parameter the callbacks receives.
 * @param[O] The type of return of the callbacks.
 */
interface LiveCallbackRegistry<I, O> {

    /**
     * Registers [callback] paired with [lifecycle] to be invoked later.
     *
     * Concrete implementations should release them when [lifecycle] be [DESTROYED]. Also, they
     * should allow callback to be invoked when appropriate, given that a [CallbackToken]
     * corresponding to the one returned is provided.
     *
     * @param[lifecycle] The [Lifecycle] which [callback] will be paired to.
     * @param[runWhileStopped] Indicates to invoke [callback] even when [lifecycle] is not active.
     * @param[callback] The callback to be registered. It is expected to be released when
     * [lifecycle] be destroyed. May be invoked only when [lifecycle] is in an active state, or in
     * whatever state until [lifecycle] be destroyed, according to [runWhileStopped]'s value.
     * @return A [CallbackToken] that identifies [callback] by it's origin, allowing it to be
     * retrieved and invoked later.
     */
    fun register(
        lifecycle: Lifecycle,
        runWhileStopped: Boolean = false,
        callback: Callback<I, O>
    ): CallbackToken<I, O>
}