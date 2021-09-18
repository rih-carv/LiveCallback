package com.ricarvalho.livecallback

import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.Lifecycle.State.STARTED
import com.ricarvalho.livecallback.lifecycle.TestLifecycle
import org.junit.Assert.assertEquals
import org.junit.Test

class LiveCallbackTest {
    @Test
    fun `callback should be invoked`() {
        var wasCalled = false
        val liveCallback = LiveCallback<String, String>(TestLifecycle(STARTED), {
            wasCalled = true
            it
        })

        liveCallback("")

        assert(wasCalled)
    }

    @Test
    fun `callback should be invoked when lifecycle is stopped`() {
        var wasCalled = false

        val liveCallback = LiveCallback<String, String>(TestLifecycle(CREATED), {
            wasCalled = true
            it
        }, true)
        liveCallback("")

        assert(wasCalled)
    }

    @Test
    fun `callback shouldn't be invoked when lifecycle is stopped`() {
        var wasNotCalled = true

        val liveCallback = LiveCallback<String, String>(TestLifecycle(CREATED), {
            wasNotCalled = false
            it
        })
        liveCallback("")

        assert(wasNotCalled)
    }

    @Test
    fun `callback shouldn't be invoked when lifecycle is destroyed`() {
        var wasNotCalled = true

        val liveCallback = LiveCallback<String, String>(TestLifecycle(DESTROYED), {
            wasNotCalled = false
            it
        })
        liveCallback("")

        assert(wasNotCalled)
    }

    @Test
    fun `callback shouldn't be invoked after lifecycle is destroyed`() {
        var wasNotCalled = true
        val lifecycle = TestLifecycle(STARTED)
        val liveCallback = LiveCallback<String, String>(lifecycle, {
            wasNotCalled = false
            it
        })

        lifecycle.state = DESTROYED
        liveCallback("")

        assert(wasNotCalled)
    }

    @Test
    fun `callback should receive correct parameter`() {
        var receivedValue: String? = null
        val liveCallback = LiveCallback<String, String>(TestLifecycle(STARTED), {
            receivedValue = it
            it
        })

        val input = "input"
        liveCallback(input)

        assertEquals(input, receivedValue)
    }

    @Test
    fun `should return correct result of callback`() {
        val output = "output"
        val liveCallback = LiveCallback<String, String>(TestLifecycle(STARTED), { output })

        val receivedValue = liveCallback("")

        assertEquals(output, receivedValue)
    }

    @Test
    fun `should return null when lifecycle is destroyed`() {
        val output = "output"
        val lifecycle = TestLifecycle(STARTED)
        val liveCallback = LiveCallback<String, String>(lifecycle, { output })

        lifecycle.state = DESTROYED
        val receivedValue = liveCallback("")

        assertEquals(null, receivedValue)
    }

    @Test
    fun `whenDestroyed should be called when lifecycle is destroyed`() {
        var wasCalled = false
        val lifecycle = TestLifecycle(STARTED)
        LiveCallback<String, String>(lifecycle, { it }) {
            wasCalled = true
        }

        lifecycle.state = DESTROYED

        assert(wasCalled)
    }

    @Test
    fun `whenDestroyed shouldn't be called when lifecycle is not destroyed`() {
        var wasNotCalled = true
        val lifecycle = TestLifecycle(STARTED)
        LiveCallback<String, String>(lifecycle, { it }) {
            wasNotCalled = false
        }

        lifecycle.state = CREATED

        assert(wasNotCalled)
    }
}
