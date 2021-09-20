package com.ricarvalho.livecallback

/**
 * A callback that receive a parameter and return a value.
 *
 * @param[I] The type of parameter the callback receives.
 * @param[O] The type of return of the callback.
 */
typealias Callback<I, O> = (input: I) -> O

/**
 * A callback that receive a parameter and return no value.
 *
 * @param[I] The type of parameter the callback receives.
 */
typealias InputCallback<I> = Callback<I, Unit>

/**
 * A callback that receive no parameters and return a value.
 *
 * @param[O] The type of return of the callback.
 */
typealias OutputCallback<O> = () -> O

/**
 * A callback that receive no parameters and return no value.
 */
typealias SimpleCallback = OutputCallback<Unit>
