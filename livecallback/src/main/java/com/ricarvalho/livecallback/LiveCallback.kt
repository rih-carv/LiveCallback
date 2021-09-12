package com.ricarvalho.livecallback

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.LifecycleOwner

/**
 * A lifecycle-aware callback wrapper.
 *
 * It respects the lifecycle of other app components, such as activities, fragments, or services.
 * This awareness ensures the wrapped callback is only invoked when appropriate. The callback is
 * invoked only when the lifecycle it is subject to is in an active state, unless [runWhileStopped]
 * is passed as `true`, in which case it is invoked at any state until [DESTROYED].
 *
 * LiveCallback considers its lifecycle as active when in the [STARTED] or [RESUMED] state.
 *
 * You can create a callback paired with an object that implements the [LifecycleOwner] interface.
 * This relationship allows the callback to be released when the state of the [lifecycle] changes to
 * [DESTROYED]. This is especially useful for activities and fragments because they can safely
 * distribute LiveCallback objects and not worry about leaks: the callback and lifecycle are
 * instantly released when their lifecycles are destroyed.
 *
 * Also, when the [lifecycle] is [DESTROYED] it invokes the [whenDestroyed] callback.
 *
 * @param[I] The type of parameter the callback receives.
 * @param[O] The type of return of the callback.
 * @param[lifecycle] The [Lifecycle] which [callback] will be paired to.
 * @param[callback] The callback to be wrapped. Will be released when [lifecycle] be destroyed. May
 * be invoked only when [lifecycle] is in an active state, or in whatever state until [lifecycle] be
 * destroyed, according to [runWhileStopped]'s value.
 * @property[runWhileStopped] Indicates to invoke [callback] even when [lifecycle] is not active.
 * @property[whenDestroyed] A callback to be invoked when [lifecycle] be [DESTROYED].
 * @constructor Creates a wrapper that relays invocations to [callback] according to [lifecycle]'s
 * current state and [runWhileStopped]'s value. Then releases [callback] and [lifecycle], and calls
 * [whenDestroyed] when [lifecycle] be destroyed.
 */
internal class LiveCallback<I, O>(
    lifecycle: Lifecycle,
    callback: Callback<I, O>,
    private val runWhileStopped: Boolean = false,
    private val whenDestroyed: ((liveCallback: LiveCallback<I, O>) -> Unit)? = null
) : DefaultLifecycleObserver, (I) -> O? {

    /**
     * The [Lifecycle] which [callback] will be paired to.
     */
    private var lifecycle: Lifecycle? = lifecycle

    /**
     * The wrapped callback, which is paired to [lifecycle].
     */
    private var callback: Callback<I, O>? = callback

    init {
        if (lifecycle.currentState == DESTROYED) finish()
        else lifecycle.addObserver(this)
    }

    /**
     * Relays invocation to [callback] if appropriate.
     *
     * Only invokes [callback] if [lifecycle] isn't [DESTROYED], and if it is in an active state or
     * [runWhileStopped] is `true`.
     *
     * @param[input] The parameter to be passed on the callback invocation.
     * @return The callback invocation return, or `null` if [lifecycle] is [DESTROYED] or in a
     * inactive state and [runWhileStopped] is `false`.
     */
    override operator fun invoke(input: I) = callback.takeIf {
        runWhileStopped || lifecycle?.currentState?.isAtLeast(STARTED) == true
    }?.invoke(input)

    /**
     * Called when [lifecycle] is [DESTROYED].
     *
     * Releases [callback] and [lifecycle], invoking [whenDestroyed] as well.
     *
     * @param[owner] The owner of [lifecycle].
     */
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        owner.lifecycle.removeObserver(this)
        finish()
    }

    /**
     * Releases [callback] and [lifecycle], invoking [whenDestroyed] as well.
     */
    private fun finish() {
        callback = null
        lifecycle = null
        whenDestroyed?.invoke(this)
    }
}