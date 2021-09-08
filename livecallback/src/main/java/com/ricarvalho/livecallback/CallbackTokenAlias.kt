package com.ricarvalho.livecallback

typealias InputCallbackToken<I> = CallbackToken<I, Unit>
typealias OutputCallbackToken<O> = CallbackToken<Void, O>
typealias SimpleCallbackToken = CallbackToken<Void, Unit>