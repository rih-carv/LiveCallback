package com.ricarvalho.livecallback.sdk

import androidx.lifecycle.liveData
import kotlinx.coroutines.delay

class LegacyCallbackBasedSdkExample {
    fun performOperation(
        success: Boolean,
        customMessage: String?,
        callback: (result: Result<String>) -> Unit
    ) = liveData<Double> {
        var percent = latestValue ?: 0.0
        while (percent < 1) {
            percent += 0.005
            emit(percent)
            delay(10)
        }
        val customMessageSuffix = " - $customMessage".takeUnless { customMessage == null }.orEmpty()
        callback(
            if (success) Result.success("Successful operation result$customMessageSuffix")
            else Result.failure(Error("Failed operation reason$customMessageSuffix"))
        )
    }
}
