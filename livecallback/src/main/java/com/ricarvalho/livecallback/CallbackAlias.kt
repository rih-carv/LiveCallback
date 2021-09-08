package com.ricarvalho.livecallback

typealias Callback<I, O> = (input: I) -> O
typealias InputCallback<I> = Callback<I, Unit>
typealias OutputCallback<O> = () -> O
typealias SimpleCallback = OutputCallback<Unit>