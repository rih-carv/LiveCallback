package com.ricarvalho.livecallback.registry

import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.Lifecycle.State.STARTED
import com.ricarvalho.livecallback.InputCallback
import com.ricarvalho.livecallback.lifecycle.TestLifecycle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class TokenizedInputLiveCallbackRegistryTest {
    private val callbacks = TokenizedInputLiveCallbackRegistry<String>()
    private val registry = callbacks as InputLiveCallbackRegistry<String>

    //region Behavior
    @Test
    fun `runs registered callback when called`() {
        var wasCalled = false

        val token = registry.register(TestLifecycle(STARTED)) { wasCalled = true }
        callbacks(token, "")

        assert(wasCalled)
    }

    @Test
    fun `runs while stopped`() {
        var wasCalled = false

        val token = registry.register(TestLifecycle(CREATED), true) { wasCalled = true }
        callbacks(token, "")

        assert(wasCalled)
    }

    @Test
    fun `doesn't runs while stopped`() {
        var wasNotCalled = true

        val token = registry.register(TestLifecycle(CREATED)) { wasNotCalled = false }
        callbacks(token, "")

        assert(wasNotCalled)
    }

    @Test
    fun `doesn't runs when lifecycle is destroyed`() {
        var wasNotCalled = true

        val token = registry.register(TestLifecycle(DESTROYED)) { wasNotCalled = false }
        callbacks(token, "")

        assert(wasNotCalled)
    }

    @Test
    fun `doesn't runs after lifecycle is destroyed`() {
        var timesCalled = 0
        val lifecycle = TestLifecycle(STARTED)

        val token = registry.register(lifecycle) { timesCalled++ }
        callbacks(token, "")
        lifecycle.state = DESTROYED
        lifecycle.state = STARTED
        callbacks(token, "")

        assertEquals(1, timesCalled)
    }

    @Test
    fun `runs as many times as called`() {
        var timesCalled = 0

        val token = registry.register(TestLifecycle(STARTED)) { timesCalled++ }
        callbacks(token, "")
        callbacks(token, "")

        assertEquals(2, timesCalled)
    }

    @Test
    fun `runs even with token of a destroyed lifecycle if callbacks are the same`() {
        var timesCalled = 0
        val lifecycle = TestLifecycle(STARTED)
        val callback: (String) -> Unit = { timesCalled++ }

        val oldToken = registry.register(lifecycle, callback = callback)
        lifecycle.state = DESTROYED
        val newToken = registry.register(TestLifecycle(STARTED), callback = callback)
        callbacks(oldToken, "")

        assertEquals(1, timesCalled)
        assertEquals(oldToken, newToken)
    }

    @Test
    fun `runs even with token of a destroyed lifecycle if callbacks origins are the same`() {
        var timesCalled = 0
        val lifecycle = TestLifecycle(STARTED)
        fun createCallback(): (String) -> Unit = { timesCalled++ }

        val oldToken = registry.register(lifecycle, callback = createCallback())
        lifecycle.state = DESTROYED
        val newToken = registry.register(TestLifecycle(STARTED), callback = createCallback())
        callbacks(oldToken, "")

        assertEquals(1, timesCalled)
        assertNotEquals(createCallback(), createCallback())
        assertEquals(oldToken, newToken)
    }

    @Test
    fun `doesn't runs with token of another callback if callbacks origins are different`() {
        var wasNotCalled = true
        val lifecycle = TestLifecycle(STARTED)

        val oldToken = registry.register(lifecycle) { wasNotCalled = false }
        lifecycle.state = DESTROYED
        val newToken = registry.register(lifecycle) { wasNotCalled = false }
        callbacks(oldToken, "")

        assert(wasNotCalled)
        assertNotEquals(oldToken, newToken)
    }
    //endregion

    //region Input
    @Test
    fun `callback input`() {
        lateinit var receivedValue: String

        val token = registry.register(TestLifecycle(STARTED)) { receivedValue = it }
        val input = "input"
        callbacks(token, input)

        assertEquals(input, receivedValue)
    }

    @Test
    fun `callback input with callback registered many times`() {
        var concatenatedValues = ""

        val callback: InputCallback<String> = { concatenatedValues += it }

        val token = registry.register(TestLifecycle(STARTED), callback = callback)
        registry.register(TestLifecycle(STARTED), callback = callback)

        val input = "input,"
        callbacks(token, input)

        assertEquals("input,input,", concatenatedValues)
    }

    @Test
    fun `callback input with many separate callbacks`() {
        lateinit var receivedValue1: String
        lateinit var receivedValue2: String

        val token1 = registry.register(TestLifecycle(STARTED)) { receivedValue1 = it }
        val token2 = registry.register(TestLifecycle(STARTED)) { receivedValue2 = it }

        val input1 = "input1"
        val input2 = "input2"
        callbacks(token1, input1)
        callbacks(token2, input2)

        assertEquals(input1, receivedValue1)
        assertEquals(input2, receivedValue2)
    }
    //endregion
}
