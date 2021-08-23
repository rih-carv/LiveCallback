package com.ricarvalho.livecallback

typealias InputCallbackToken<I> = CallbackToken<I, Unit>
typealias OutputCallbackToken<O> = CallbackToken<Void, O>

@kotlin.jvm.JvmInline
value class CallbackToken<I, O> private constructor(val token: String) {
    internal constructor(callback: (I) -> O) : this(callback.token)

    companion object {
        internal fun <I> input(callback: (I) -> Unit) = InputCallbackToken<I>(callback.token)

        internal fun <O> output(callback: () -> O) = OutputCallbackToken<O>(callback.token)

        private val Any.token get() = this::class.java.name
    }
}