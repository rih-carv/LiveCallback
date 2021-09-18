package com.ricarvalho.livecallback.sdk

import androidx.lifecycle.liveData
import kotlinx.coroutines.delay

class LegacyCallbackBasedSdkExample {
    fun performOperation(success: Boolean, callback: (result: Result<String>) -> Unit) =
        liveData<Double> {
            var percent = latestValue ?: 0.0
            while (percent < 1) {
                percent += 0.005
                emit(percent)
                delay(10)
            }
            callback(
                if (success) Result.success("Successful operation result")
                else Result.failure(Error("Failed operation reason"))
            )
        }
}
