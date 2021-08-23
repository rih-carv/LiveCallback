package com.ricarvalho.livecallback

@kotlin.jvm.JvmInline
value class CallbackToken<I, O> private constructor(val token: String) {
    internal constructor(callback: (I) -> O) : this(callback::class.java.name)
}