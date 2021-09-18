package com.ricarvalho.livecallback

/**
 * A normalized identification of a callback's origin, agnostic to its instance.
 *
 * This is meant to be used with callbacks that receive a parameter and return a value.
 * For other kinds of callbacks, there are more specific versions:
 * - [InputCallbackToken]: for callbacks that receive a parameter and return no value.
 * - [OutputCallbackToken]: for callbacks that receive no parameters and return a value.
 * - [SimpleCallbackToken]: for callbacks that receive no parameters and return no value.
 *
 * @param[I] The type of parameter the callback receives.
 * @param[O] The type of return of the callback.
 * @property[token] The internal [String] representation.
 * @constructor Creates a new token with an internal [String] representation.
 */
@kotlin.jvm.JvmInline
value class CallbackToken<I, O> private constructor(private val token: String) {

    /**
     * Creates a [CallbackToken] that identifies [callback]'s origin.
     *
     * @param[I] The type of parameter the callback receives.
     * @param[O] The type of return of the callback.
     * @param[callback] The callback which will be identified by the created token.
     */
    internal constructor(callback: Callback<I, O>) : this(callback.token)

    internal companion object {

        /**
         * Convenience method to create an [InputCallbackToken] that identifies [callback]'s origin.
         *
         * @param[I] The type of parameter the callback receives.
         * @param[callback] The callback which will be identified by the returned token.
         * @return An [InputCallbackToken] that identifies the received callback's origin.
         */
        internal fun <I> input(callback: InputCallback<I>) = InputCallbackToken<I>(callback.token)

        /**
         * Convenience method to create an [OutputCallbackToken] that identifies [callback]'s origin.
         *
         * @param[O] The type of return of the callback.
         * @param[callback] The callback which will be identified by the returned token.
         * @return An [OutputCallbackToken] that identifies the received callback's origin.
         */
        internal fun <O> output(callback: OutputCallback<O>) = OutputCallbackToken<O>(callback.token)

        /**
         * Convenience method to create a [SimpleCallbackToken] that identifies [callback]'s origin.
         *
         * @param[callback] The callback which will be identified by the returned token.
         * @return A [SimpleCallbackToken] that identifies the received callback's origin.
         */
        internal fun simple(callback: SimpleCallback) = SimpleCallbackToken(callback.token)

        /**
         * A way to identify callbacks origins as a [String]
         */
        private val Any.token get() = this::class.java.name
    }
}
