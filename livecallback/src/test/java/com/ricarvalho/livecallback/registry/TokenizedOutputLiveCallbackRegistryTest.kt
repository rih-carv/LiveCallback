package com.ricarvalho.livecallback.registry

import androidx.lifecycle.Lifecycle.State.*
import com.ricarvalho.livecallback.lifecycle.TestLifecycle
import org.junit.Assert.*
import org.junit.Test

class TokenizedOutputLiveCallbackRegistryTest {
    private val callbacks = TokenizedOutputLiveCallbackRegistry<String>()
    private val registry = callbacks as OutputLiveCallbackRegistry<String>

    //region Behavior
    @Test
    fun `runs registered callback when called`() {
        var wasCalled = false

        val token = registry.register(TestLifecycle(STARTED)) {
            wasCalled = true
            ""
        }
        callbacks(token)

        assert(wasCalled)
    }

    @Test
    fun `runs while stopped`() {
        var wasCalled = false

        val token = registry.register(TestLifecycle(CREATED), true) {
            wasCalled = true
            ""
        }
        callbacks(token)

        assert(wasCalled)
    }

    @Test
    fun `doesn't runs while stopped`() {
        var wasNotCalled = true

        val token = registry.register(TestLifecycle(CREATED)) {
            wasNotCalled = false
            ""
        }
        callbacks(token)

        assert(wasNotCalled)
    }

    @Test
    fun `doesn't runs when lifecycle is destroyed`() {
        var wasNotCalled = true

        val token = registry.register(TestLifecycle(DESTROYED)) {
            wasNotCalled = false
            ""
        }
        callbacks(token)

        assert(wasNotCalled)
    }

    @Test
    fun `doesn't runs after lifecycle is destroyed`() {
        var timesCalled = 0
        val lifecycle = TestLifecycle(STARTED)

        val token = registry.register(lifecycle) {
            timesCalled++
            ""
        }
        callbacks(token)
        lifecycle.state = DESTROYED
        lifecycle.state = STARTED
        callbacks(token)

        assertEquals(1, timesCalled)
    }

    @Test
    fun `runs as many times as called`() {
        var timesCalled = 0

        val token = registry.register(TestLifecycle(STARTED)) {
            timesCalled++
            ""
        }
        callbacks(token)
        callbacks(token)

        assertEquals(2, timesCalled)
    }

    @Test
    fun `runs even with token of a destroyed lifecycle if callbacks are the same`() {
        var timesCalled = 0
        val lifecycle = TestLifecycle(STARTED)
        val callback: () -> String = {
            timesCalled++
            ""
        }

        val oldToken = registry.register(lifecycle, callback = callback)
        lifecycle.state = DESTROYED
        val newToken = registry.register(TestLifecycle(STARTED), callback = callback)
        callbacks(oldToken)

        assertEquals(1, timesCalled)
        assertEquals(oldToken, newToken)
    }

    @Test
    fun `runs even with token of a destroyed lifecycle if callbacks origins are the same`() {
        var timesCalled = 0
        val lifecycle = TestLifecycle(STARTED)
        fun createCallback(): () -> String = {
            timesCalled++
            ""
        }

        val oldToken = registry.register(lifecycle, callback = createCallback())
        lifecycle.state = DESTROYED
        val newToken = registry.register(TestLifecycle(STARTED), callback = createCallback())
        callbacks(oldToken)

        assertEquals(1, timesCalled)
        assertNotEquals(createCallback(), createCallback())
        assertEquals(oldToken, newToken)
    }

    @Test
    fun `doesn't runs with token of another callback if callbacks origins are different`() {
        var wasNotCalled = true
        val lifecycle = TestLifecycle(STARTED)

        val oldToken = registry.register(lifecycle) {
            wasNotCalled = false
            ""
        }
        lifecycle.state = DESTROYED
        val newToken = registry.register(lifecycle) {
            wasNotCalled = false
            ""
        }
        callbacks(oldToken)

        assert(wasNotCalled)
        assertNotEquals(oldToken, newToken)
    }
    //endregion

    //region Output
    @Test
    fun `callback output`() {
        val output = "output"

        val token = registry.register(TestLifecycle(STARTED)) { output }
        val receivedValues = callbacks(token)

        assertEquals(output, receivedValues.single())
    }

    @Test
    fun `callback output after destroyed`() {
        val output = "output"
        val lifecycle = TestLifecycle(STARTED)

        val token = registry.register(lifecycle) { output }
        lifecycle.state = DESTROYED
        val receivedValues = callbacks(token)

        assert(receivedValues.isEmpty())
    }

    @Test
    fun `callback output with callback registered many times`() {
        val output = "output"
        val callback: () -> String = { output }

        val token = registry.register(TestLifecycle(STARTED), callback = callback)
        registry.register(TestLifecycle(STARTED), callback = callback)
        val receivedValues = callbacks(token)

        assertEquals(2, receivedValues.size)
        assert(receivedValues.all { it == output })
    }

    @Test
    fun `callback output with many dynamic callbacks`() {
        val output1 = "output1"
        val output2 = "output2"
        fun callback(output: String): () -> String = { output }

        val token = registry.register(TestLifecycle(STARTED), callback = callback(output1))
        registry.register(TestLifecycle(STARTED), callback = callback(output2))
        val receivedValues = callbacks(token)

        assertEquals(2, receivedValues.size)
        assertEquals(output1, receivedValues.firstOrNull())
        assertEquals(output2, receivedValues.lastOrNull())
    }

    @Test
    fun `callback output with many separate callbacks`() {
        val output1 = "output1"
        val output2 = "output2"

        val token1 = registry.register(TestLifecycle(STARTED)) { output1 }
        val token2 = registry.register(TestLifecycle(STARTED)) { output2 }
        val receivedValues1 = callbacks(token1)
        val receivedValues2 = callbacks(token2)

        assertEquals(output1, receivedValues1.single())
        assertEquals(output2, receivedValues2.single())
    }

    @Test
    fun `callback output after some destroyed`() {
        val output1 = "output1"
        val output2 = "output2"
        val lifecycle1 = TestLifecycle(STARTED)
        val lifecycle2 = TestLifecycle(STARTED)
        fun callback(output: String): () -> String = { output }

        val token = registry.register(lifecycle1, callback = callback(output1))
        registry.register(lifecycle2, callback = callback(output2))
        lifecycle1.state = DESTROYED
        val receivedValues = callbacks(token)

        assertEquals(output2, receivedValues.single())
    }
    //endregion
}