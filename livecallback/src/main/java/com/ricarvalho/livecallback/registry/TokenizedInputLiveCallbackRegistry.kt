package com.ricarvalho.livecallback.registry

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.*
import androidx.lifecycle.LifecycleOwner
import com.ricarvalho.livecallback.InputCallback
import com.ricarvalho.livecallback.InputCallbackToken
import com.ricarvalho.livecallback.LiveCallbackContainer

/**
 * An auto-releasing registry of [LiveCallbackContainer]s that correlates callbacks and its origins.
 *
 * It respects the lifecycle of other app components, such as activities, fragments, or services.
 * This awareness ensures the registered callbacks are only invoked when appropriate.
 *
 * You can register a callback paired with an object that implements the [LifecycleOwner] interface.
 * This relationship allows the callback to be released when the state of the lifecycle changes to
 * [DESTROYED]. This is especially useful for activities and fragments because they can safely
 * register callbacks and not worry about leaks: the callback and lifecycle are instantly released
 * when their lifecycles are destroyed.
 *
 * Registered callbacks can be invoked when appropriate, given that an [InputCallbackToken]
 * corresponding to the one returned by [register] is provided on [invoke].
 *
 * This is meant to be used with callbacks that receive a parameter and return no value.
 * For other kinds of callbacks, there are more specific versions:
 * - [TokenizedLiveCallbackRegistry]: for callbacks that receive a parameter and return a value.
 * - [TokenizedOutputLiveCallbackRegistry]: for callbacks that receive no parameters and return a
 * value.
 * - [TokenizedSimpleLiveCallbackRegistry]: for callbacks that receive no parameters and return no
 * value.
 *
 * @param[I] The type of parameter the callbacks receives.
 * @property[registry] The container that keeps the registered callbacks paired with respective
 * [Lifecycle]s into [LiveCallbackContainer]s identified by [InputCallbackToken]s.
 * @constructor Creates a registry with the provided container, that relays invocations to the
 * registered callbacks based on the provided [InputCallbackToken]s. The register creates
 * [LiveCallbackContainer]s as needed to keep callbacks and their associated [Lifecycle], then
 * removes a container when all its contained [Lifecycle]s are [DESTROYED].
 */
@JvmInline
value class TokenizedInputLiveCallbackRegistry<I> private constructor(
    private val registry: TokenizedLiveCallbackRegistry<I, Unit>
) : InputLiveCallbackRegistry<I>, (InputCallbackToken<I>, I) -> Unit {

    /**
     * Creates an empty registry that relays invocations to the registered callbacks based on the
     * provided [InputCallbackToken]s.
     *
     * The register creates [LiveCallbackContainer]s as needed to keep callbacks and their
     * associated [Lifecycle], then removes a container when all its contained [Lifecycle]s are
     * [DESTROYED].
     */
    constructor() : this(TokenizedLiveCallbackRegistry())

    /**
     * Registers [callback] paired with [lifecycle] to be invoked later, and releases them when
     * [lifecycle] is [DESTROYED].
     *
     * [callback] can be invoked when appropriate, given that an [InputCallbackToken] corresponding
     * to the one returned is provided on [invoke].
     *
     * It respects the lifecycle of other app components, such as activities, fragments, or services.
     * This ensures [callback] is only invoked when [lifecycle] is in an active state, unless
     * [runWhileStopped] is passed as `true`, in which case it is invoked at any state until
     * [DESTROYED].
     *
     * [lifecycle] is considered to be active when in the [STARTED] or [RESUMED] state.
     *
     * @param[lifecycle] The [Lifecycle] which [callback] will be paired to.
     * @param[runWhileStopped] Indicates to invoke [callback] even when [lifecycle] is not active.
     * @param[callback] The callback to be registered. Will be released when [lifecycle] be
     * destroyed. May be invoked only when [lifecycle] is in an active state, or in whatever state
     * until [lifecycle] be destroyed, according to [runWhileStopped]'s value.
     * @return An [InputCallbackToken] that identifies [callback] by it's origin, allowing it to be
     * retrieved and invoked later.
     */
    override fun register(
        lifecycle: Lifecycle,
        runWhileStopped: Boolean,
        callback: InputCallback<I>
    ) = registry.register(lifecycle, runWhileStopped, callback)

    /**
     * Relays invocations to the registered callbacks whose tokens matches [token].
     *
     * The return values of registered callbacks that are in inactive states (unless otherwise
     * indicated by runWhileStopped when being registered) or that returned null are discarded and
     * will not be included in the returned list.
     *
     * @param[token] The token identifying the registered callbacks that should be invoked.
     * @param[input] The parameter to be passed on the callback invocations.
     */
    override operator fun invoke(token: InputCallbackToken<I>, input: I) {
        registry(token, input)
    }
}