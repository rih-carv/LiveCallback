package com.ricarvalho.livecallback

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.DESTROYED

/**
 * An auto-releasing container of lifecycle-aware callbacks.
 *
 * It creates and store [LiveCallback]s instances to pair the added callbacks with [Lifecycle]s.
 * Invocations are relayed to the added callbacks, and returns the returned values of the active
 * [LiveCallback]s.
 *
 * When the associated [Lifecycle]s of all the added callbacks are [DESTROYED], it invokes the
 * [whenAllBeDestroyed] callback.
 *
 * @param[I] The type of parameter the callbacks receives.
 * @param[O] The type of return of the callbacks.
 * @property[whenAllBeDestroyed] A callback to be invoked when the associated [Lifecycle]s of all
 * the added callbacks be [DESTROYED].
 * @constructor Creates an empty container that relays invocations to the added callbacks. Then
 * removes those callbacks when their associated [Lifecycle]s are [DESTROYED], and then calls
 * [whenAllBeDestroyed] when all of the previously added callbacks [Lifecycle]s be destroyed.
 */
internal class LiveCallbackContainer<I, O>(
    private val whenAllBeDestroyed: ((container: LiveCallbackContainer<I, O>) -> Unit)? = null
) : (I) -> List<O> {

    /**
     * The container that keeps the added callbacks paired with respective [Lifecycle]s.
     */
    private val callbacks = mutableListOf<LiveCallback<I, O>>()

    /**
     * Adds and store [callback] paired with [lifecycle], and releases them when [lifecycle] is
     * [DESTROYED].
     *
     * @param[lifecycle] The [Lifecycle] which [callback] will be paired to.
     * @param[runWhileStopped] Indicates to invoke [callback] even when [lifecycle] is not active.
     * @param[callback] The callback to be added. Will be released when [lifecycle] be destroyed.
     * May be invoked only when [lifecycle] is in an active state, or in whatever state until
     * [lifecycle] be destroyed, according to [runWhileStopped]'s value.
     */
    fun add(lifecycle: Lifecycle, runWhileStopped: Boolean = false, callback: Callback<I, O>) {
        callbacks += LiveCallback(lifecycle, callback, runWhileStopped, whenDestroyed = {
            callbacks.remove(it)
            if (callbacks.isEmpty()) whenAllBeDestroyed?.invoke(this)
        })
    }

    /**
     * Relays invocations to the added callbacks.
     *
     * The return values of added callbacks that are in inactive states (unless otherwise indicated
     * by `runWhileStopped` when being added) or that returned null are discarded and will not be
     * included in the returned list.
     *
     * @param[input] The parameter to be passed on the callback invocations.
     * @return The returned values of the callbacks with active [Lifecycle]s.
     */
    override operator fun invoke(input: I) = callbacks.mapNotNull { it(input) }
}
