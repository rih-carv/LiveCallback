package com.ricarvalho.livecallback

/**
 * A [CallbackToken] for callbacks that receive a parameter and return no value.
 *
 * @param[I] The type of parameter the callback receives.
 */
typealias InputCallbackToken<I> = CallbackToken<I, Unit>

/**
 * A [CallbackToken] for callbacks that receive no parameters and return a value.
 *
 * @param[O] The type of return of the callback.
 */
typealias OutputCallbackToken<O> = CallbackToken<Void, O>

/**
 * A [CallbackToken] for callbacks that receive no parameters and return no value.
 */
typealias SimpleCallbackToken = CallbackToken<Void, Unit>
