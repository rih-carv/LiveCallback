package com.ricarvalho.livecallback

@kotlin.jvm.JvmInline
value class CallbackToken<I, O> private constructor(private val token: String) {
    internal constructor(callback: Callback<I, O>) : this(callback.token)

    internal companion object {
        internal fun <I> input(callback: InputCallback<I>) = InputCallbackToken<I>(callback.token)

        internal fun <O> output(callback: OutputCallback<O>) = OutputCallbackToken<O>(callback.token)

        internal fun simple(callback: SimpleCallback) = SimpleCallbackToken(callback.token)

        private val Any.token get() = this::class.java.name
    }
}