package com.ricarvalho.livecallback.registry

import com.ricarvalho.livecallback.CallbackToken

interface CallbackTokenContainer<I, O> {
    infix operator fun contains(token: CallbackToken<I, O>): Boolean
}