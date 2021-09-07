package com.ricarvalho.livecallback.postponable

import androidx.lifecycle.Lifecycle
import com.ricarvalho.livecallback.CallbackToken
import com.ricarvalho.livecallback.InputCallbackToken
import com.ricarvalho.livecallback.OutputCallbackToken
import com.ricarvalho.livecallback.SimpleCallbackToken
import com.ricarvalho.livecallback.registry.CallbackTokenContainer
import com.ricarvalho.livecallback.registry.LiveCallbackRegistry
import com.ricarvalho.livecallback.registry.OutputLiveCallbackRegistry

fun <C : CallbackTokenContainer<I, O>, I, O> C.postponable() =
    Postponable<C, CallbackToken<I, O>>(this) { container, token -> token in container }

fun <R : LiveCallbackRegistry<I, O>, I, O> Postponable<R, CallbackToken<I, O>>.register(
    lifecycle: Lifecycle, runWhileStopped: Boolean = false, callback: (input: I) -> O
) = relay { it.register(lifecycle, runWhileStopped, callback) }

fun <R : OutputLiveCallbackRegistry<O>, O> Postponable<R, OutputCallbackToken<O>>.register(
    lifecycle: Lifecycle, runWhileStopped: Boolean = false, callback: () -> O
) = relay { it.register(lifecycle, runWhileStopped, callback) }

operator fun <F : (T, I) -> Unit, T : InputCallbackToken<I>, I> Postponable<F, T>.invoke(
    token: T, input: I, whenDone: () -> Unit = {}
) = invoke(token, { it(token, input) }) { _, _ -> whenDone() }

operator fun <F : (T, I) -> List<O>, T : CallbackToken<I, O>, I, O> Postponable<F, T>.invoke(
    token: T, input: I, whenDone: (result: List<O>) -> Unit = {}
) = invoke(token, { it(token, input) }) { _, result -> whenDone(result) }

operator fun <F : (T) -> List<O>, T : OutputCallbackToken<O>, O> Postponable<F, T>.invoke(
    token: T, whenDone: (result: List<O>) -> Unit = {}
) = invoke(token, { it(token) }) { _, result -> whenDone(result) }

operator fun <F : (T) -> Unit, T : SimpleCallbackToken> Postponable<F, T>.invoke(
    token: T, whenDone: () -> Unit = {}
) = invoke(token, { it(token) }) { _, _ -> whenDone() }

/*
fun test(r: TokenizedInputLiveCallbackRegistry<String>, t: InputCallbackToken<String>, l: Lifecycle) {
    val postponable = r.postponable()
    postponable.register(l) {

    }
    postponable(t, "") {

    }
}

fun test(r: TokenizedLiveCallbackRegistry<String, Int>, t: CallbackToken<String, Int>, l: Lifecycle) {
    val postponable = r.postponable()
    postponable.register(l) {
        0
    }
    postponable(t, "") {

    }
}

fun test(r: TokenizedOutputLiveCallbackRegistry<Int>, t: OutputCallbackToken<Int>, l: Lifecycle) {
    val postponable = r.postponable()
    postponable.register(l) {
        0
    }
    postponable(t) {

    }
}

fun test(r: TokenizedSimpleLiveCallbackRegistry, t: SimpleCallbackToken, l: Lifecycle) {
    val postponable = r.postponable()
    postponable.register(l) {

    }
    postponable(t) {

    }
    r.postponable()(t) {

    }
}
*/