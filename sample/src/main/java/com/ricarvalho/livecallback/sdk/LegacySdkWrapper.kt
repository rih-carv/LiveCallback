package com.ricarvalho.livecallback.sdk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.ricarvalho.livecallback.InputCallbackToken
import com.ricarvalho.livecallback.registry.InputLiveCallbackRegistry
import com.ricarvalho.livecallback.registry.TokenizedInputLiveCallbackRegistry

object LegacySdkWrapper {
    private var togglingResult = true
    private val legacySdk = LegacyCallbackBasedSdkExample()
    private val callbacks = TokenizedInputLiveCallbackRegistry<Result<String>>()
    private val progressLiveData = MutableLiveData<LiveData<Double>>()

    val registry = callbacks as InputLiveCallbackRegistry<Result<String>>
    val progress = progressLiveData.switchMap { progress ->
        progress.map { (it * 100).toInt().coerceIn(0..100) }
    }

    fun <T : InputCallbackToken<Result<String>>> perform(
        vararg tokens: T,
        expectedResult: Boolean = togglingResult
    ) {
        progressLiveData.value = legacySdk.performOperation(expectedResult) { result ->
            for (token in tokens) callbacks(token, result)
        }
        togglingResult = !expectedResult
    }
}
