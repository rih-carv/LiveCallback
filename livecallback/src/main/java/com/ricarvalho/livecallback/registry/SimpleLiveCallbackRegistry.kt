package com.ricarvalho.livecallback.registry

import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.LifecycleOwner

/**
 * A lifecycle-aware registry of callbacks that correlates callbacks and its origins.
 *
 * This is expected to be used with callbacks that receive no parameters and return no value.
 *
 * It should respect the lifecycle of other app components, such as activities, fragments, or
 * services. This awareness ensure the registered callbacks should be only invoked when appropriate.
 *
 * You can register a callback paired with an object that implements the [LifecycleOwner] interface.
 * This relationship allows the callback to be released when the state of the lifecycle changes to
 * [DESTROYED]. This is especially useful for activities and fragments because they can safely
 * register callbacks and not worry about leaks: the callback and lifecycle are expected to be
 * instantly released when their lifecycles are destroyed.
 */
interface SimpleLiveCallbackRegistry : OutputLiveCallbackRegistry<Unit>
