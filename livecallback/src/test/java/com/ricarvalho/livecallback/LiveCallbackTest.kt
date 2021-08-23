package com.ricarvalho.livecallback

import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.Lifecycle.State.STARTED
import com.ricarvalho.livecallback.lifecycle.TestLifecycle
import org.junit.Assert.assertEquals
import org.junit.Test

class LiveCallbackTest {
    @Test
    fun `callback should be invoked`() {
        var wasCalled = false
        val liveCallback = LiveCallback<String, String>(TestLifecycle(STARTED)) {
            wasCalled = true
            it
        }

        liveCallback.invoke("")

        assert(wasCalled)
    }

    @Test
    fun `callback shouldn't be invoked when lifecycle is destroyed`() {
        var wasNotCalled = true

        val liveCallback = LiveCallback<String, String>(TestLifecycle(DESTROYED)) {
            wasNotCalled = false
            it
        }
        liveCallback.invoke("")

        assert(wasNotCalled)
    }

    @Test
    fun `callback shouldn't be invoked after lifecycle is destroyed`() {
        var wasNotCalled = true
        val lifecycle = TestLifecycle(STARTED)
        val liveCallback = LiveCallback<String, String>(lifecycle) {
            wasNotCalled = false
            it
        }

        lifecycle.state = DESTROYED
        liveCallback.invoke("")

        assert(wasNotCalled)
    }

    @Test
    fun `callback should receive correct parameter`() {
        var receivedValue: String? = null
        val liveCallback = LiveCallback<String, String>(TestLifecycle(STARTED)) {
            receivedValue = it
            it
        }

        val input = "input"
        liveCallback.invoke(input)

        assertEquals(input, receivedValue)
    }

    @Test
    fun `should return correct result of callback`() {
        val output = "output"
        val liveCallback = LiveCallback<String, String>(TestLifecycle(STARTED)) { output }

        val receivedValue = liveCallback.invoke("")

        assertEquals(output, receivedValue)
    }

    @Test
    fun `should return null when lifecycle is destroyed`() {
        val output = "output"
        val lifecycle = TestLifecycle(STARTED)
        val liveCallback = LiveCallback<String, String>(lifecycle) { output }

        lifecycle.state = DESTROYED
        val receivedValue = liveCallback.invoke("")

        assertEquals(null, receivedValue)
    }
}