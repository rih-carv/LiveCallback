package com.ricarvalho.livecallback

import androidx.lifecycle.Lifecycle.State.*
import com.ricarvalho.livecallback.lifecycle.TestLifecycle
import org.junit.Assert.*
import org.junit.Test

class TokenizedLiveCallbackRegistryTest {
    private val callbacks = TokenizedLiveCallbackRegistry<String, String>()

    //region Behavior
    @Test
    fun `runs registered callback when called`() {
        var wasCalled = false

        val token = callbacks.register(TestLifecycle(STARTED)) {
            wasCalled = true
            ""
        }
        callbacks(token, "")

        assert(wasCalled)
    }

    @Test
    fun `runs while stopped`() {
        var wasCalled = false

        val token = callbacks.register(TestLifecycle(CREATED), true) {
            wasCalled = true
            ""
        }
        callbacks(token, "")

        assert(wasCalled)
    }

    @Test
    fun `doesn't runs while stopped`() {
        var wasNotCalled = true

        val token = callbacks.register(TestLifecycle(CREATED)) {
            wasNotCalled = false
            ""
        }
        callbacks(token, "")

        assert(wasNotCalled)
    }

    @Test
    fun `doesn't runs when lifecycle is destroyed`() {
        var wasNotCalled = true

        val token = callbacks.register(TestLifecycle(DESTROYED)) {
            wasNotCalled = false
            ""
        }
        callbacks(token, "")

        assert(wasNotCalled)
    }

    @Test
    fun `doesn't runs after lifecycle is destroyed`() {
        var timesCalled = 0
        val lifecycle = TestLifecycle(STARTED)

        val token = callbacks.register(lifecycle) {
            timesCalled++
            ""
        }
        callbacks(token, "")
        lifecycle.state = DESTROYED
        lifecycle.state = STARTED
        callbacks(token, "")

        assertEquals(1, timesCalled)
    }

    @Test
    fun `runs as many times as called`() {
        var timesCalled = 0

        val token = callbacks.register(TestLifecycle(STARTED)) {
            timesCalled++
            ""
        }
        callbacks(token, "")
        callbacks(token, "")

        assertEquals(2, timesCalled)
    }

    @Test
    fun `runs even with token of a destroyed lifecycle if callbacks are the same`() {
        var timesCalled = 0
        val lifecycle = TestLifecycle(STARTED)
        val callback: (String) -> String = {
            timesCalled++
            ""
        }

        val oldToken = callbacks.register(lifecycle, callback = callback)
        lifecycle.state = DESTROYED
        val newToken = callbacks.register(TestLifecycle(STARTED), callback = callback)
        callbacks(oldToken, "")

        assertEquals(1, timesCalled)
        assertEquals(oldToken, newToken)
    }

    @Test
    fun `runs even with token of a destroyed lifecycle if callbacks origins are the same`() {
        var timesCalled = 0
        val lifecycle = TestLifecycle(STARTED)
        fun createCallback(): (String) -> String = {
            timesCalled++
            ""
        }

        val oldToken = callbacks.register(lifecycle, callback = createCallback())
        lifecycle.state = DESTROYED
        val newToken = callbacks.register(TestLifecycle(STARTED), callback = createCallback())
        callbacks(oldToken, "")

        assertEquals(1, timesCalled)
        assertNotEquals(createCallback(), createCallback())
        assertEquals(oldToken, newToken)
    }

    @Test
    fun `doesn't runs with token of another callback if callbacks origins are different`() {
        var wasNotCalled = true
        val lifecycle = TestLifecycle(STARTED)

        val oldToken = callbacks.register(lifecycle) {
            wasNotCalled = false
            ""
        }
        lifecycle.state = DESTROYED
        val newToken = callbacks.register(lifecycle) {
            wasNotCalled = false
            ""
        }
        callbacks(oldToken, "")

        assert(wasNotCalled)
        assertNotEquals(oldToken, newToken)
    }
    //endregion

    //region Input
    @Test
    fun `callback input`() {
        lateinit var receivedValue: String

        val token = callbacks.register(TestLifecycle(STARTED)) {
            receivedValue = it
            ""
        }
        val input = "input"
        callbacks(token, input)

        assertEquals(input, receivedValue)
    }

    @Test
    fun `callback input with callback registered many times`() {
        var concatenatedValues = ""

        val callback: (String) -> String = {
            concatenatedValues += it
            ""
        }

        val token = callbacks.register(TestLifecycle(STARTED), callback = callback)
        callbacks.register(TestLifecycle(STARTED), callback = callback)

        val input = "input,"
        callbacks(token, input)

        assertEquals("input,input,", concatenatedValues)
    }

    @Test
    fun `callback input with many separate callbacks`() {
        lateinit var receivedValue1: String
        lateinit var receivedValue2: String

        val token1 = callbacks.register(TestLifecycle(STARTED)) {
            receivedValue1 = it
            ""
        }
        val token2 = callbacks.register(TestLifecycle(STARTED)) {
            receivedValue2 = it
            ""
        }

        val input1 = "input1"
        val input2 = "input2"
        callbacks(token1, input1)
        callbacks(token2, input2)

        assertEquals(input1, receivedValue1)
        assertEquals(input2, receivedValue2)
    }
    //endregion

    //region Output
    @Test
    fun `callback output`() {
        val output = "output"

        val token = callbacks.register(TestLifecycle(STARTED)) { output }
        val receivedValues = callbacks(token, "")

        assertEquals(output, receivedValues.single())
    }

    @Test
    fun `callback output after destroyed`() {
        val output = "output"
        val lifecycle = TestLifecycle(STARTED)

        val token = callbacks.register(lifecycle) { output }
        lifecycle.state = DESTROYED
        val receivedValues = callbacks(token, "")

        assert(receivedValues.isEmpty())
    }

    @Test
    fun `callback output with callback registered many times`() {
        val output = "output"
        val callback: (String) -> String = { output }

        val token = callbacks.register(TestLifecycle(STARTED), callback = callback)
        callbacks.register(TestLifecycle(STARTED), callback = callback)
        val receivedValues = callbacks(token, "")

        assertEquals(2, receivedValues.size)
        assert(receivedValues.all { it == output })
    }

    @Test
    fun `callback output with many dynamic callbacks`() {
        val output1 = "output1"
        val output2 = "output2"
        fun callback(output: String): (String) -> String = { output }

        val token = callbacks.register(TestLifecycle(STARTED), callback = callback(output1))
        callbacks.register(TestLifecycle(STARTED), callback = callback(output2))
        val receivedValues = callbacks(token, "")

        assertEquals(2, receivedValues.size)
        assertEquals(output1, receivedValues.firstOrNull())
        assertEquals(output2, receivedValues.lastOrNull())
    }

    @Test
    fun `callback output with many separate callbacks`() {
        val output1 = "output1"
        val output2 = "output2"

        val token1 = callbacks.register(TestLifecycle(STARTED)) { output1 }
        val token2 = callbacks.register(TestLifecycle(STARTED)) { output2 }
        val receivedValues1 = callbacks(token1, "")
        val receivedValues2 = callbacks(token2, "")

        assertEquals(output1, receivedValues1.single())
        assertEquals(output2, receivedValues2.single())
    }

    @Test
    fun `callback output after some destroyed`() {
        val output1 = "output1"
        val output2 = "output2"
        val lifecycle1 = TestLifecycle(STARTED)
        val lifecycle2 = TestLifecycle(STARTED)
        fun callback(output: String): (String) -> String = { output }

        val token = callbacks.register(lifecycle1, callback = callback(output1))
        callbacks.register(lifecycle2, callback = callback(output2))
        lifecycle1.state = DESTROYED
        val receivedValues = callbacks(token, "")

        assertEquals(output2, receivedValues.single())
    }
    //endregion
}